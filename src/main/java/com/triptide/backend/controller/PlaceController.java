package com.triptide.backend.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String tags) {
        Set<String> tagSet = tags != null ? 
            Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toSet()) 
            : null;
        List<PlaceBasicDTO> places = placeService.getPlacesByType(type, limit, tagSet);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/{placeId}/details")
    public PlaceDetailedDTO getPlaceDetails(@PathVariable String placeId) {
        return placeService.getPlaceDetails(placeId);
    }

    @GetMapping("/{placeId}/basic")
    public PlaceBasicDTO getPlaceBasicDetails(@PathVariable String placeId) {
        return placeService.getPlaceBasicDetails(placeId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceBasicDTO>> searchPlaces(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String tags) {
        Set<String> tagSet = tags != null ? 
            Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toSet()) 
            : null;
        List<PlaceBasicDTO> matchingPlaces = placeService.searchPlacesByName(name, page, tagSet);
        return ResponseEntity.ok(matchingPlaces);
    }

    @GetMapping("/tags")
    public ResponseEntity<Set<String>> getAllTags(@RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(placeService.getAllUniqueTagsByType(type));
        }
        return ResponseEntity.ok(placeService.getAllUniqueTags());
    }
} 