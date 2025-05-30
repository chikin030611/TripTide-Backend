package com.triptide.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItineraryRequest {
    private List<ScheduledPlaceDTO> scheduledPlaces;
} 