package com.triptide.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.triptide.backend.dto.PlaceBasicDTO;
import com.triptide.backend.service.TouristAttractionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    
    private final TouristAttractionService touristAttractionService;

    @GetMapping
    public ResponseEntity<List<PlaceBasicDTO>> getPlaces(
            @RequestParam String type,
            @RequestParam(defaultValue = "5") int limit) {
        List<PlaceBasicDTO> places = touristAttractionService.getPlacesByType(type, limit);
        return ResponseEntity.ok(places);
    }
} 