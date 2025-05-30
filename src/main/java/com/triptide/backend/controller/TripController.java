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

import com.triptide.backend.dto.AddPlaceToTripRequest;
import com.triptide.backend.dto.CreateTripRequest;
import com.triptide.backend.dto.TripResponseDTO;
import com.triptide.backend.dto.UpdateTripRequest;
import com.triptide.backend.model.Trip;
import com.triptide.backend.service.TripService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody CreateTripRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Get the username/email from principal
        Trip createdTrip = tripService.createTrip(request, userEmail);
        return ResponseEntity.ok(createdTrip);
    }

    @GetMapping
    public ResponseEntity<List<Trip>> getUserTrips() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<Trip> trips = tripService.getUserTrips(userEmail);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> getTripById(@PathVariable String tripId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Trip trip = tripService.getTripById(tripId, userEmail);
        return ResponseEntity.ok(convertToResponseDto(trip));
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable String tripId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        tripService.deleteTrip(tripId, userEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tripId}/places")
    public ResponseEntity<Trip> addPlaceToTrip(
            @PathVariable String tripId,
            @RequestBody AddPlaceToTripRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Trip updatedTrip = tripService.addPlaceToTrip(tripId, request, userEmail);
        return ResponseEntity.ok(updatedTrip);
    }

    @DeleteMapping("/{tripId}/places/{placeId}")
    public ResponseEntity<Trip> removePlaceFromTrip(
            @PathVariable String tripId,
            @PathVariable String placeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Trip updatedTrip = tripService.removePlaceFromTrip(tripId, placeId, userEmail);
        return ResponseEntity.ok(updatedTrip);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<Trip> updateTrip(
            @PathVariable String tripId,
            @RequestBody UpdateTripRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Trip updatedTrip = tripService.updateTrip(tripId, request, userEmail);
        return ResponseEntity.ok(updatedTrip);
    }

    @GetMapping("/{tripId}/places/{placeId}/check")
    public ResponseEntity<Boolean> checkPlaceInTrip(
            @PathVariable String tripId,
            @PathVariable String placeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        boolean isInTrip = tripService.isPlaceInTrip(tripId, placeId, userEmail);
        return ResponseEntity.ok(isInTrip);
    }

    private TripResponseDTO convertToResponseDto(Trip trip) {
        TripResponseDTO dto = new TripResponseDTO();
        dto.setId(trip.getId());
        dto.setName(trip.getName());
        dto.setDescription(trip.getDescription());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setTouristAttractionIds(trip.getTouristAttractionIds());
        dto.setRestaurantIds(trip.getRestaurantIds());
        dto.setLodgingIds(trip.getLodgingIds());
        dto.setImage(trip.getImage());
        dto.setDailyItineraries(trip.getDailyItineraries());
        return dto;
    }
} 