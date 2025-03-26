package com.triptide.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.triptide.backend.dto.CreateItineraryRequest;
import com.triptide.backend.dto.UpdateItineraryRequest;
import com.triptide.backend.model.DailyItinerary;
import com.triptide.backend.service.ItineraryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class ItineraryController {

    private final ItineraryService itineraryService;

    /**
     * Create a new itinerary for a specific day in a trip
     * 
     * POST /api/trips/:id/itineraries
     */
    @PostMapping("/{tripId}/itineraries")
    public ResponseEntity<DailyItinerary> createItinerary(
            @PathVariable String tripId,
            @RequestBody CreateItineraryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        DailyItinerary createdItinerary = itineraryService.createItinerary(tripId, request, userEmail);
        return ResponseEntity.ok(createdItinerary);
    }

    /**
     * Get all itineraries for a trip
     * 
     * GET /api/trips/:id/itineraries
     */
    @GetMapping("/{tripId}/itineraries")
    public ResponseEntity<List<DailyItinerary>> getTripItineraries(
            @PathVariable String tripId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<DailyItinerary> itineraries = itineraryService.getTripItineraries(tripId, userEmail);
        return ResponseEntity.ok(itineraries);
    }

    /**
     * Update an itinerary for a specific day in a trip
     * 
     * PUT /api/trips/:id/itineraries/:day
     */
    @PutMapping("/{tripId}/itineraries/{day}")
    public ResponseEntity<DailyItinerary> updateItinerary(
            @PathVariable String tripId,
            @PathVariable Integer day,
            @RequestBody UpdateItineraryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        DailyItinerary updatedItinerary = itineraryService.updateItinerary(tripId, day, request, userEmail);
        return ResponseEntity.ok(updatedItinerary);
    }

    /**
     * Delete an itinerary for a specific day in a trip
     * 
     * DELETE /api/trips/:id/itineraries/:day
     */
    @DeleteMapping("/{tripId}/itineraries/{day}")
    public ResponseEntity<Void> deleteItinerary(
            @PathVariable String tripId,
            @PathVariable Integer day) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        itineraryService.deleteItinerary(tripId, day, userEmail);
        return ResponseEntity.noContent().build();
    }
} 