package com.triptide.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.triptide.backend.dto.AddPlaceToTripRequest;
import com.triptide.backend.dto.CreateTripRequest;
import com.triptide.backend.model.AppUser;
import com.triptide.backend.model.Trip;
import com.triptide.backend.repository.AppUserRepository;
import com.triptide.backend.repository.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripService {
    
    private final TripRepository tripRepository;
    private final AppUserRepository appUserRepository;

    @Transactional
    public Trip createTrip(CreateTripRequest request, String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Trip trip = new Trip();
        trip.setName(request.getName());
        trip.setDescription(request.getDescription());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setImage(request.getImage());
        trip.setUser(user);
        
        return tripRepository.save(trip);
    }

    @Transactional(readOnly = true)
    public List<Trip> getUserTrips(String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return tripRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Trip getTripById(String tripId, String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));

        // Check if the trip belongs to the requesting user
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return trip;
    }

    @Transactional
    public void deleteTrip(String tripId, String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        tripRepository.deleteById(tripId);
    }

    @Transactional
    public Trip addPlaceToTrip(String tripId, AddPlaceToTripRequest request, String userEmail) {
        Trip trip = getTripById(tripId, userEmail); // This will handle auth check

        switch (request.getPlaceType().toLowerCase()) {
            case "tourist_attraction" -> {
                if (trip.getTouristAttractionIds() == null) {
                    trip.setTouristAttractionIds(new ArrayList<>());
                }
                trip.getTouristAttractionIds().add(request.getPlaceId());
            }

            case "restaurant" -> {
                if (trip.getRestaurantIds() == null) {
                    trip.setRestaurantIds(new ArrayList<>());
                }
                trip.getRestaurantIds().add(request.getPlaceId());
            }

            case "lodging" -> {
                if (trip.getLodgingIds() == null) {
                    trip.setLodgingIds(new ArrayList<>());
                }
                trip.getLodgingIds().add(request.getPlaceId());
            }

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Invalid place type. Must be tourist attraction, restaurant, or lodging"
                );
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip removePlaceFromTrip(String tripId, String placeId, String userEmail) {
        Trip trip = getTripById(tripId, userEmail);
        boolean placeRemoved = false;

        if (trip.getTouristAttractionIds() != null) {
            placeRemoved |= trip.getTouristAttractionIds().remove(placeId);
        }
        if (trip.getRestaurantIds() != null) {
            placeRemoved |= trip.getRestaurantIds().remove(placeId);
        }
        if (trip.getLodgingIds() != null) {
            placeRemoved |= trip.getLodgingIds().remove(placeId);
        }

        if (!placeRemoved) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found in trip");
        }

        return tripRepository.save(trip);
    }
    
} 