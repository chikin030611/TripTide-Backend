package com.triptide.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetailedDTO {
    private String id;
    private String name;
    private String type;
    private String[] tags;
    private String address;
    private Double rating;
    private String ratingCount;
    private OpeningHours openingHours;
    private String description;
    private List<String> photos;
    private Double latitude;
    private Double longitude;

    @Data
    @Builder
    public static class Date {
        private int year;
        private int month;
        private int day;
    }

    @Data
    @Builder
    public static class TimeSlot {
        private int day;
        private int hour;
        private int minute;
        private Date date;
    }

    @Data
    @Builder
    public static class Period {
        private TimeSlot open;
        private TimeSlot close;
    }

    @Data
    @Builder
    public static class OpeningHours {
        private List<Period> periods;
    }
} 

