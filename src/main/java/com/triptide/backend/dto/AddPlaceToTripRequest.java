package com.triptide.backend.dto;

import lombok.Data;

@Data
public class AddPlaceToTripRequest {
    private String placeId;
    private String placeType; // "tourist attraction", "restaurant", or "lodging"
} 