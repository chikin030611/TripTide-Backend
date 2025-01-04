package com.triptide.backend.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceBasicDTO {
    private String placeId;
    private String name;
    private List<String> types;
    private String photoUrl;
    private Double rating;
} 