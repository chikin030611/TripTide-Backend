package com.triptide.backend.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledPlaceDTO {
    private String placeId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
} 