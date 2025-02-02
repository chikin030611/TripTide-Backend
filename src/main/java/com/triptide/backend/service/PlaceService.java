package com.triptide.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.triptide.backend.dto.PlaceBasicDTO;
import com.triptide.backend.dto.PlaceDetailedDTO;
import com.triptide.backend.dto.PlaceDetailedDTO.Date;
import com.triptide.backend.dto.PlaceDetailedDTO.OpeningHours;
import com.triptide.backend.dto.PlaceDetailedDTO.Period;
import com.triptide.backend.dto.PlaceDetailedDTO.TimeSlot;
import com.triptide.backend.model.BasePlace;
import com.triptide.backend.model.Lodging;
import com.triptide.backend.model.Restaurant;
import com.triptide.backend.model.Tag;
import com.triptide.backend.model.TouristAttraction;
import com.triptide.backend.repository.LodgingRepository;
import com.triptide.backend.repository.RestaurantRepository;
import com.triptide.backend.repository.TouristAttractionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
    
    private final TouristAttractionRepository touristAttractionRepository;
    private final RestaurantRepository restaurantRepository;
    private final LodgingRepository lodgingRepository;
    private final GooglePlacesService googlePlacesService;

    public List<PlaceBasicDTO> getPlacesByType(String type, int limit, String tag) {
        List<? extends BasePlace> places;
        
        switch (type.toLowerCase()) {
            case "tourist_attraction":
                if (tag != null) {
                    places = touristAttractionRepository.findByTagsName(tag, PageRequest.of(0, limit)).getContent();
                } else {
                    long touristAttractionCount = touristAttractionRepository.count();
                    int randomPageTA = (int) (Math.random() * (touristAttractionCount / limit));
                    places = touristAttractionRepository.findAll(PageRequest.of(randomPageTA, limit)).getContent();
                }
                break;
            case "restaurant":
                if (tag != null) {
                    places = restaurantRepository.findByTagsName(tag, PageRequest.of(0, limit)).getContent();
                } else {
                    long restaurantCount = restaurantRepository.count();
                    int randomPageR = (int) (Math.random() * (restaurantCount / limit));
                    places = restaurantRepository.findAll(PageRequest.of(randomPageR, limit)).getContent();
                }
                break;
            case "lodging":
                if (tag != null) {
                    places = lodgingRepository.findByTagsName(tag, PageRequest.of(0, limit)).getContent();
                } else {
                    long lodgingCount = lodgingRepository.count();
                    int randomPageL = (int) (Math.random() * (lodgingCount / limit));
                    places = lodgingRepository.findAll(PageRequest.of(randomPageL, limit)).getContent();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid place type: " + type);
        }

        return places.stream()
            .map(this::convertToBasicDTO)
            .collect(Collectors.toList());
    }

    private Set<Tag> getTagsFromPlace(BasePlace place) {
        if (place instanceof Restaurant) {
            return ((Restaurant) place).getTags();
        } else if (place instanceof TouristAttraction) {
            return ((TouristAttraction) place).getTags();
        } else if (place instanceof Lodging) {
            return ((Lodging) place).getTags();
        }
        return Set.of();
    }

    private String[] convertTagsToArray(Set<Tag> tags) {
        return tags.stream()
            .map(Tag::getName)
            .toArray(String[]::new);
    }

    private PlaceBasicDTO convertToBasicDTO(BasePlace place) {
        JsonNode placeDetails = googlePlacesService.getBasicPlaceDetails(place.getPlaceId());
        
        String photoUrl = null;
        if (placeDetails.has("photos") && placeDetails.get("photos").size() > 0) {
            String photoReference = placeDetails.get("photos").get(0).get("name").asText();
            photoUrl = googlePlacesService.getPhotoUrl(photoReference);
        }

        Double rating = placeDetails.has("rating") ? placeDetails.get("rating").asDouble() : null;
        String[] tagNames = convertTagsToArray(getTagsFromPlace(place));

        return PlaceBasicDTO.builder()
            .placeId(place.getPlaceId())
            .name(place.getName())
            .tags(tagNames)
            .photoUrl(photoUrl)
            .rating(rating)
            .build();
    }

    public PlaceDetailedDTO getPlaceDetails(String placeId) {
        BasePlace place = touristAttractionRepository.findByPlaceId(placeId)
            .map(p -> (BasePlace) p)
            .orElseGet(() -> restaurantRepository.findByPlaceId(placeId)
                .map(p -> (BasePlace) p)
                .orElseGet(() -> lodgingRepository.findByPlaceId(placeId)
                    .map(p -> (BasePlace) p)
                    .orElseThrow(() -> new RuntimeException("Place not found: " + placeId))));

        // Get detailed data from Google API
        JsonNode placeDetails = googlePlacesService.getDetailedPlaceData(placeId);
        String[] tagNames = convertTagsToArray(getTagsFromPlace(place));

        // Extract photo URLs
        List<String> photoUrls = new ArrayList<>();
        if (placeDetails.has("photos")) {
            placeDetails.get("photos").forEach(photo -> {
                String photoReference = photo.get("name").asText();
                photoUrls.add(googlePlacesService.getPhotoUrl(photoReference));
            });
        }

        // Extract opening hours
        OpeningHours openingHours = null;
        if (placeDetails.has("currentOpeningHours")) {
            JsonNode openingHoursNode = placeDetails.get("currentOpeningHours");
            List<Period> periods = new ArrayList<>();
            
            if (openingHoursNode.has("periods")) {
                openingHoursNode.get("periods").forEach(period -> {
                    periods.add(Period.builder()
                        .open(extractTimeSlot(period.get("open")))
                        .close(extractTimeSlot(period.get("close")))
                        .build());
                });
            }

            openingHours = OpeningHours.builder()
                .periods(periods)
                .build();
        }

        // Build and return the DTO
        return PlaceDetailedDTO.builder()
            .id(place.getPlaceId())
            .name(place.getName())
            .tags(tagNames)
            .address(placeDetails.get("formattedAddress").asText())
            .rating(placeDetails.has("rating") ? placeDetails.get("rating").asDouble() : null)
            .openingHours(openingHours)
            .description(placeDetails.has("editorialSummary") ? 
                placeDetails.get("editorialSummary").get("text").asText() : null)
            .photos(photoUrls)
            .latitude(placeDetails.get("location").get("latitude").asDouble())
            .longitude(placeDetails.get("location").get("longitude").asDouble())
            .build();
    }

    private TimeSlot extractTimeSlot(JsonNode timeSlotNode) {
        return TimeSlot.builder()
            .day(timeSlotNode.get("day").asInt())
            .hour(timeSlotNode.get("hour").asInt())
            .minute(timeSlotNode.get("minute").asInt())
            .date(Date.builder()
                .year(timeSlotNode.get("date").get("year").asInt())
                .month(timeSlotNode.get("date").get("month").asInt())
                .day(timeSlotNode.get("date").get("day").asInt())
                .build())
            .build();
    }

    public List<PlaceBasicDTO> searchPlacesByName(String name, int page) {
        // Get paginated results from tourist attractions
        PageRequest pageRequest = PageRequest.of(page, 10); // limit of 10 items per page
        List<BasePlace> matchingPlaces = new ArrayList<>();

        matchingPlaces.addAll(
            touristAttractionRepository.findByNameContainingIgnoreCase(name, pageRequest).getContent()
        );
        matchingPlaces.addAll(
            restaurantRepository.findByNameContainingIgnoreCase(name, pageRequest).getContent()
        );

        matchingPlaces.addAll(
            lodgingRepository.findByNameContainingIgnoreCase(name, pageRequest).getContent()
        );

        // Convert all matching places to DTOs
        return matchingPlaces.stream()
            .map(this::convertToBasicDTO)
            .collect(Collectors.toList());
    }
} 