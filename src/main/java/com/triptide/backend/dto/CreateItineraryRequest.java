package com.triptide.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItineraryRequest {
    private Integer day;
    private List<ScheduledPlaceDTO> scheduledPlaces;
} 