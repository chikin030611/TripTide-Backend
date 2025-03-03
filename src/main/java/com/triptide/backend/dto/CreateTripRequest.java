package com.triptide.backend.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CreateTripRequest {
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String image;
} 