package com.triptide.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.triptide.backend.dto.PlaceBasicDTO;
import com.triptide.backend.dto.PlaceDetailedDTO;
import com.triptide.backend.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    
    private final PlaceService placeService;

    @GetMapping
    public ResponseEntity<List<PlaceBasicDTO>> getPlaces(
            @RequestParam String type,
            @RequestParam(defaultValue = "5") int limit) {
        List<PlaceBasicDTO> places = placeService.getPlacesByType(type, limit);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{placeId}/details")
    public PlaceDetailedDTO getPlaceDetails(@PathVariable String placeId) {
        return placeService.getPlaceDetails(placeId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceBasicDTO>> searchPlaces(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page) {
        List<PlaceBasicDTO> matchingPlaces = placeService.searchPlacesByName(name, page);
        return ResponseEntity.ok(matchingPlaces);
    }
} 