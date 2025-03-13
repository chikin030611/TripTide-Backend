package com.triptide.backend.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.triptide.backend.model.DailyItinerary;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TripResponseDto {
    private String id;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private List<String> touristAttractionIds;
    private List<String> restaurantIds;
    private List<String> lodgingIds;
    private String image;
    
    @JsonManagedReference
    private List<DailyItinerary> dailyItineraries;
} 