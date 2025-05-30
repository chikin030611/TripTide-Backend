package com.triptide.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.triptide.backend.dto.PlaceBasicDTO;
import com.triptide.backend.dto.UserPreferenceDTO;
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
public class RecommendationService {
    
    private final LodgingRepository lodgingRepository;
    private final RestaurantRepository restaurantRepository;
    private final TouristAttractionRepository touristAttractionRepository;
    private final UserPreferenceService userPreferenceService;
    private final GooglePlacesService googlePlacesService;
    private static final int MAX_TAGS_FOR_RECOMMENDATIONS = 3;
    private static final int MAX_TOTAL_RECOMMENDATIONS = 6;

    public List<PlaceBasicDTO> getRecommendedPlaces(String userEmail) {
        // Get user preferences
        UserPreferenceDTO preferences = userPreferenceService.getUserPreferences(userEmail);
        Set<String> preferredTags = preferences.getPreferredTags();

        // Randomly select 3 tags if user has more than 3 preferences
        Set<String> selectedTags = selectRandomTags(preferredTags);

        // Get recommendations from each repository using selected tags
        List<PlaceBasicDTO> lodgings = getLodgingRecommendations(selectedTags);
        List<PlaceBasicDTO> restaurants = getRestaurantRecommendations(selectedTags);
        List<PlaceBasicDTO> attractions = getTouristAttractionRecommendations(selectedTags);

        // Combine all recommendations
        List<PlaceBasicDTO> allRecommendations = new ArrayList<>();
        allRecommendations.addAll(lodgings);
        allRecommendations.addAll(restaurants);
        allRecommendations.addAll(attractions);

        // Sort by number of matching tags (descending)
        Collections.sort(allRecommendations, (a, b) -> {
            long aMatchCount = countMatchingTags(a.getTags(), selectedTags);
            long bMatchCount = countMatchingTags(b.getTags(), selectedTags);
            return Long.compare(bMatchCount, aMatchCount);
        });

        // Return top recommendations
        return allRecommendations.stream()
            .limit(MAX_TOTAL_RECOMMENDATIONS)
            .collect(Collectors.toList());
    }

    private long countMatchingTags(String[] placeTags, Set<String> selectedTags) {
        return Arrays.stream(placeTags)
            .filter(selectedTags::contains)
            .count();
    }

    private Set<String> selectRandomTags(Set<String> allTags) {
        if (allTags.size() <= MAX_TAGS_FOR_RECOMMENDATIONS) {
            return allTags;
        }

        // Convert Set to List for random selection
        List<String> tagList = new ArrayList<>(allTags);
        Random random = new Random();
        
        // Randomly select 3 tags
        return random.ints(0, tagList.size())
            .distinct()
            .limit(MAX_TAGS_FOR_RECOMMENDATIONS)
            .mapToObj(tagList::get)
            .collect(Collectors.toSet());
    }

    private List<PlaceBasicDTO> getLodgingRecommendations(Set<String> preferredTags) {
        List<Lodging> lodgings = lodgingRepository.findByTags(preferredTags);
        return lodgings.stream()
            .map(this::convertLodgingToDTO)
            .collect(Collectors.toList());
    }

    private List<PlaceBasicDTO> getRestaurantRecommendations(Set<String> preferredTags) {
        List<Restaurant> restaurants = restaurantRepository.findByTags(preferredTags);
        return restaurants.stream()
            .map(this::convertRestaurantToDTO)
            .collect(Collectors.toList());
    }

    private List<PlaceBasicDTO> getTouristAttractionRecommendations(Set<String> preferredTags) {
        List<TouristAttraction> attractions = touristAttractionRepository.findByTags(preferredTags);
        return attractions.stream()
            .map(this::convertAttractionToDTO)
            .collect(Collectors.toList());
    }

    private PlaceBasicDTO convertLodgingToDTO(Lodging lodging) {
        JsonNode placeDetails = googlePlacesService.getBasicPlaceDetails(lodging.getPlaceId());
        String photoUrl = null;
        if (placeDetails.has("photos") && placeDetails.get("photos").size() > 0) {
            String photoReference = placeDetails.get("photos").get(0).get("name").asText();
            photoUrl = googlePlacesService.getPhotoUrl(photoReference);
        }
        return PlaceBasicDTO.builder()
            .placeId(lodging.getPlaceId())
            .name(lodging.getName())
            .tags(lodging.getTags().stream()
                .map(Tag::getName)
                .toArray(String[]::new))
            .type("LODGING")
            .photoUrl(photoUrl)
            .rating(placeDetails.get("rating").asDouble())
            .ratingCount(placeDetails.get("userRatingCount").asText())
            .build();
    }

    private PlaceBasicDTO convertRestaurantToDTO(Restaurant restaurant) {
        JsonNode placeDetails = googlePlacesService.getBasicPlaceDetails(restaurant.getPlaceId());
        String photoUrl = null;
        if (placeDetails.has("photos") && placeDetails.get("photos").size() > 0) {
            String photoReference = placeDetails.get("photos").get(0).get("name").asText();
            photoUrl = googlePlacesService.getPhotoUrl(photoReference);
        }
        return PlaceBasicDTO.builder()
            .placeId(restaurant.getPlaceId())
            .name(restaurant.getName())
            .tags(restaurant.getTags().stream()
                .map(Tag::getName)
                .toArray(String[]::new))
            .type("RESTAURANT")
            .photoUrl(photoUrl)
            .rating(placeDetails.get("rating").asDouble())
            .ratingCount(placeDetails.get("userRatingCount").asText())
            .build();
    }

    private PlaceBasicDTO convertAttractionToDTO(TouristAttraction attraction) {
        JsonNode placeDetails = googlePlacesService.getBasicPlaceDetails(attraction.getPlaceId());
        String photoUrl = null;
        if (placeDetails.has("photos") && placeDetails.get("photos").size() > 0) {
            String photoReference = placeDetails.get("photos").get(0).get("name").asText();
            photoUrl = googlePlacesService.getPhotoUrl(photoReference);
        }
        return PlaceBasicDTO.builder()
            .placeId(attraction.getPlaceId())
            .name(attraction.getName())
            .tags(attraction.getTags().stream()
                .map(Tag::getName)
                .toArray(String[]::new))
            .type("TOURIST_ATTRACTION")
            .photoUrl(photoUrl)
            .rating(placeDetails.get("rating").asDouble())
            .ratingCount(placeDetails.get("userRatingCount").asText())
            .build();
    }
} 