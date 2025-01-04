package com.triptide.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.triptide.backend.dto.PlaceBasicDTO;
import com.triptide.backend.model.BasePlace;
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

        return PlaceBasicDTO.builder()
            .placeId(place.getPlaceId())
            .name(place.getName())
            .tags(place.getTags())
            .photoUrl(photoUrl)
            .rating(rating)
            .build();
    }
} 