package com.triptide.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.triptide.backend.dto.AddPlaceToTripRequest;
import com.triptide.backend.dto.CreateTripRequest;
import com.triptide.backend.dto.UpdateTripRequest;
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
        
        trip.setTouristAttractionIds(new ArrayList<>());
        trip.setRestaurantIds(new ArrayList<>());
        trip.setLodgingIds(new ArrayList<>());
        
        return tripRepository.save(trip);
    }

    @Transactional(readOnly = true)
    public List<Trip> getUserTrips(String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        List<Trip> trips = tripRepository.findByUser(user);
        
        // Sort scheduledPlaces by startTime in each dailyItinerary
        for (Trip trip : trips) {
            if (trip.getDailyItineraries() != null) {
                for (com.triptide.backend.model.DailyItinerary itinerary : trip.getDailyItineraries()) {
                    if (itinerary.getScheduledPlaces() != null) {
                        itinerary.getScheduledPlaces().sort(java.util.Comparator.comparing(
                            com.triptide.backend.model.ScheduledPlace::getStartTime));
                    }
                }
            }
        }
        
        return trips;
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

        // Sort scheduledPlaces by startTime in each dailyItinerary
        if (trip.getDailyItineraries() != null) {
            for (com.triptide.backend.model.DailyItinerary itinerary : trip.getDailyItineraries()) {
                if (itinerary.getScheduledPlaces() != null) {
                    itinerary.getScheduledPlaces().sort(java.util.Comparator.comparing(
                        com.triptide.backend.model.ScheduledPlace::getStartTime));
                }
            }
        }

        return trip;
    }

    @Transactional
    public void deleteTrip(String tripId, String userEmail) {
        appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        tripRepository.deleteById(tripId);
    }

    @Transactional
    public Trip addPlaceToTrip(String tripId, AddPlaceToTripRequest request, String userEmail) {
        Trip trip = getTripById(tripId, userEmail); // This will handle auth check

        if (isPlaceInTrip(tripId, request.getPlaceId(), userEmail)) {
            System.out.println("Place already in trip");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Place already in trip");
        }

        String placeType = request.getPlaceType().toLowerCase();
        boolean placeAdded = false;

        // Add to appropriate list based on place type
        switch (placeType) {
            case "tourist_attraction" -> {
                if (trip.getTouristAttractionIds() == null) {
                    trip.setTouristAttractionIds(new ArrayList<>());
                }
                placeAdded = trip.getTouristAttractionIds().add(request.getPlaceId());
                System.out.println("Place added to tourist attraction list");
            }
            case "restaurant" -> {
                if (trip.getRestaurantIds() == null) {
                    trip.setRestaurantIds(new ArrayList<>());
                }
                placeAdded = trip.getRestaurantIds().add(request.getPlaceId());
            }
            case "lodging" -> {
                if (trip.getLodgingIds() == null) {
                    trip.setLodgingIds(new ArrayList<>());
                }
                placeAdded = trip.getLodgingIds().add(request.getPlaceId());
            }
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid place type. Must be tourist attraction, restaurant, or lodging"
            );
        }

        if (!placeAdded) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to add place to trip");
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip removePlaceFromTrip(String tripId, String placeId, String userEmail) {
        Trip trip = getTripById(tripId, userEmail);
        boolean placeRemoved = false;

        // Try to remove from each list, tracking if removal was successful
        if (trip.getTouristAttractionIds() != null && trip.getTouristAttractionIds().contains(placeId)) {
            placeRemoved = trip.getTouristAttractionIds().remove(placeId);
        } else if (trip.getRestaurantIds() != null && trip.getRestaurantIds().contains(placeId)) {
            placeRemoved = trip.getRestaurantIds().remove(placeId);
        } else if (trip.getLodgingIds() != null && trip.getLodgingIds().contains(placeId)) {
            placeRemoved = trip.getLodgingIds().remove(placeId);
        }

        if (!placeRemoved) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Place not found in trip");
        }

        return tripRepository.save(trip);
    }

    @Transactional
    public Trip updateTrip(String tripId, UpdateTripRequest request, String userEmail) {
        Trip trip = getTripById(tripId, userEmail); // This will handle auth check
        
        if (request.getName() != null) {
            trip.setName(request.getName());
        }
        if (request.getDescription() != null) {
            trip.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            trip.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            trip.setEndDate(request.getEndDate());
        }
        if (request.getImage() != null) {
            trip.setImage(request.getImage());
        }
        
        return tripRepository.save(trip);
    }

    @Transactional(readOnly = true)
    public boolean isPlaceInTrip(String tripId, String placeId, String userEmail) {
        Trip trip = getTripById(tripId, userEmail); // This will handle auth check
        
        return (trip.getTouristAttractionIds() != null && trip.getTouristAttractionIds().contains(placeId)) ||
               (trip.getRestaurantIds() != null && trip.getRestaurantIds().contains(placeId)) ||
               (trip.getLodgingIds() != null && trip.getLodgingIds().contains(placeId));
    }
} 