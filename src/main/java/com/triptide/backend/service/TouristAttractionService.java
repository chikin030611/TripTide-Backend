package com.triptide.backend.service;

import java.util.ArrayList;
import java.util.List;
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
import com.triptide.backend.model.Tag;
import com.triptide.backend.model.TouristAttraction;
import com.triptide.backend.repository.LodgingRepository;
import com.triptide.backend.repository.RestaurantRepository;
import com.triptide.backend.repository.TouristAttractionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TouristAttractionService {
    
    private final TouristAttractionRepository touristAttractionRepository;
    private final RestaurantRepository restaurantRepository;
    private final LodgingRepository lodgingRepository;
    private final GooglePlacesService googlePlacesService;

    public List<PlaceBasicDTO> getPlacesByType(String type, int limit) {
        List<? extends BasePlace> places;
        
        switch (type.toLowerCase()) {
            case "tourist_attraction":
                places = touristAttractionRepository.findAll(PageRequest.of(0, limit)).getContent();
                break;
            case "restaurant":
                places = restaurantRepository.findAll(PageRequest.of(0, limit)).getContent();
                break;
            case "lodging":
                places = lodgingRepository.findAll(PageRequest.of(0, limit)).getContent();
                break;
            default:
                throw new IllegalArgumentException("Invalid place type: " + type);
        }

        return places.stream()
            .map(this::convertToBasicDTO)
            .collect(Collectors.toList());
    }

    private PlaceBasicDTO convertToBasicDTO(BasePlace place) {
        JsonNode placeDetails = googlePlacesService.getBasicPlaceDetails(place.getPlaceId());
        
        String photoUrl = null;
        if (placeDetails.has("photos") && placeDetails.get("photos").size() > 0) {
            String photoReference = placeDetails.get("photos").get(0).get("name").asText();
            photoUrl = googlePlacesService.getPhotoUrl(photoReference);
        }

        Double rating = placeDetails.has("rating") ? placeDetails.get("rating").asDouble() : null;

        // Convert TouristAttraction tags to string array
        String[] tagNames = null;
        if (place instanceof TouristAttraction) {
            tagNames = ((TouristAttraction) place).getTags().stream()
                .map(Tag::getName)
                .toArray(String[]::new);
        }

        return PlaceBasicDTO.builder()
            .placeId(place.getPlaceId())
            .name(place.getName())
            .tags(tagNames)
            .photoUrl(photoUrl)
            .rating(rating)
            .build();
    }

    public PlaceDetailedDTO getPlaceDetails(String placeId) {
        // Find the place in database
        BasePlace place = touristAttractionRepository.findByPlaceId(placeId)
            .orElseThrow(() -> new RuntimeException("Place not found: " + placeId));

        // Extract types
        // Convert TouristAttraction tags to string array
        String[] tagNames = null;
        if (place instanceof TouristAttraction) {
            tagNames = ((TouristAttraction) place).getTags().stream()
                .map(Tag::getName)
                .toArray(String[]::new);
        }

        // Get detailed data from Google API
        JsonNode placeDetails = googlePlacesService.getDetailedPlaceData(placeId);

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
        List<BasePlace> matchingPlaces = new ArrayList<>(
            touristAttractionRepository.findByNameContainingIgnoreCase(name, pageRequest).getContent()
        );

        // Convert all matching places to DTOs
        return matchingPlaces.stream()
            .map(this::convertToBasicDTO)
            .collect(Collectors.toList());
    }
} 