package com.triptide.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBasicDTO {
    private String placeId;
    private String name;
    private String type;
    private String[] tags;
    private String photoUrl;
    private Double rating;
    private String ratingCount;
} 