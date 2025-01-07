package com.triptide.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GooglePlacesService {
    
    @Value("${google.places.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public JsonNode getBasicPlaceDetails(String placeId) {
        String url = String.format(
            "https://places.googleapis.com/v1/places/%s?fields=rating,photos&key=%s",
            placeId, apiKey
        );
        
        String response = restTemplate.getForObject(url, String.class);
        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Google Places API response", e);
        }
    }

    public String getPhotoUrl(String photoReference) {
        return String.format(
            "https://places.googleapis.com/v1/%s/media?maxHeightPx=4000&maxWidthPx=2800&key=",
            photoReference
        );
    }

    public JsonNode getDetailedPlaceData(String placeId) {
        String url = String.format(
            "https://places.googleapis.com/v1/places/%s?fields=photos,rating,currentOpeningHours,formattedAddress,editorialSummary,location&key=%s",
            placeId, apiKey
        );
        
        String response = restTemplate.getForObject(url, String.class);
        try {
            return objectMapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Google Places API response", e);
        }
    }
} 

