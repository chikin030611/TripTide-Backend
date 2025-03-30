package com.triptide.backend.service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.triptide.backend.dto.CreateItineraryRequest;
import com.triptide.backend.dto.ScheduledPlaceDTO;
import com.triptide.backend.dto.UpdateItineraryRequest;
import com.triptide.backend.model.AppUser;
import com.triptide.backend.model.DailyItinerary;
import com.triptide.backend.model.ScheduledPlace;
import com.triptide.backend.model.Trip;
import com.triptide.backend.repository.AppUserRepository;
import com.triptide.backend.repository.DailyItineraryRepository;
import com.triptide.backend.repository.ScheduledPlaceRepository;
import com.triptide.backend.repository.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItineraryService {
    
    private final TripRepository tripRepository;
    private final AppUserRepository appUserRepository;
    private final DailyItineraryRepository dailyItineraryRepository;
    private final ScheduledPlaceRepository scheduledPlaceRepository;

    @Transactional
    public DailyItinerary createItinerary(String tripId, CreateItineraryRequest request, String userEmail) {
        // Verify user owns the trip
        Trip trip = getTripByIdAndUserEmail(tripId, userEmail);
        
        // Calculate trip duration in days
        long durationInMillis = trip.getEndDate().getTime() - trip.getStartDate().getTime();
        int durationInDays = (int) (durationInMillis / (24 * 60 * 60 * 1000)) + 1;
        
        // Validate day number
        if (request.getDay() < 1 || request.getDay() > durationInDays) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Day number must be between 1 and " + durationInDays);
        }
        
        // Check if an itinerary already exists for this day
        Optional<DailyItinerary> existingItinerary = trip.getDailyItineraries().stream()
                .filter(itinerary -> itinerary.getDay().equals(request.getDay()))
                .findFirst();
        
        if (existingItinerary.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "An itinerary already exists for day " + request.getDay());
        }
        
        // Validate scheduled places timestamps and check for overlaps
        if (request.getScheduledPlaces() != null && !request.getScheduledPlaces().isEmpty()) {
            validateScheduledPlaces(request.getScheduledPlaces());
        }
        
        // Create new daily itinerary
        DailyItinerary dailyItinerary = new DailyItinerary();
        dailyItinerary.setDay(request.getDay());
        dailyItinerary.setTrip(trip);
        dailyItinerary.setScheduledPlaces(new ArrayList<>());
        
        // Calculate and set the date based on trip start date and day number
        long dayInMillis = (request.getDay() - 1) * 24 * 60 * 60 * 1000L;
        java.util.Date itineraryDate = new java.util.Date(trip.getStartDate().getTime() + dayInMillis);
        dailyItinerary.setDate(itineraryDate);
        
        // Save the daily itinerary
        DailyItinerary savedItinerary = dailyItineraryRepository.save(dailyItinerary);
        
        // Add scheduled places
        if (request.getScheduledPlaces() != null && !request.getScheduledPlaces().isEmpty()) {
            for (ScheduledPlaceDTO placeDto : request.getScheduledPlaces()) {
                ScheduledPlace scheduledPlace = new ScheduledPlace();
                scheduledPlace.setPlaceId(placeDto.getPlaceId());
                scheduledPlace.setStartTime(Time.valueOf(placeDto.getStartTime()));
                scheduledPlace.setEndTime(Time.valueOf(placeDto.getEndTime()));
                scheduledPlace.setNotes(placeDto.getNotes());
                scheduledPlace.setDailyItinerary(savedItinerary);
                
                // Set the date from the daily itinerary
                scheduledPlace.setDate(savedItinerary.getDate());
                
                scheduledPlaceRepository.save(scheduledPlace);
            }
        }
        
        // Refresh the itinerary to include the scheduled places
        DailyItinerary refreshedItinerary = dailyItineraryRepository.findById(savedItinerary.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved itinerary"));
        
        // Sort scheduled places by start time
        if (refreshedItinerary.getScheduledPlaces() != null) {
            refreshedItinerary.getScheduledPlaces().sort(Comparator.comparing(ScheduledPlace::getStartTime));
        }
        
        return refreshedItinerary;
    }
    
    @Transactional(readOnly = true)
    public List<DailyItinerary> getTripItineraries(String tripId, String userEmail) {
        // Verify user owns the trip
        Trip trip = getTripByIdAndUserEmail(tripId, userEmail);
        
        // Return all daily itineraries for the trip
        List<DailyItinerary> itineraries = dailyItineraryRepository.findByTripId(tripId);
        
        // Sort scheduled places in each itinerary by start time
        for (DailyItinerary itinerary : itineraries) {
            if (itinerary.getScheduledPlaces() != null) {
                itinerary.getScheduledPlaces().sort(Comparator.comparing(ScheduledPlace::getStartTime));
            }
        }
        
        return itineraries;
    }
    
    @Transactional
    public DailyItinerary updateItinerary(String tripId, Integer day, UpdateItineraryRequest request, String userEmail) {
        // Verify user owns the trip
        Trip trip = getTripByIdAndUserEmail(tripId, userEmail);
        
        // Calculate trip duration in days
        long durationInMillis = trip.getEndDate().getTime() - trip.getStartDate().getTime();
        int durationInDays = (int) (durationInMillis / (24 * 60 * 60 * 1000)) + 1;
        
        // Validate day number
        if (day < 1 || day > durationInDays) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Day number must be between 1 and " + durationInDays);
        }
        
        // Find the daily itinerary for the specified day
        DailyItinerary dailyItinerary = trip.getDailyItineraries().stream()
                .filter(itinerary -> itinerary.getDay().equals(day))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "No itinerary found for day " + day));
        
        // Validate scheduled places timestamps and check for overlaps
        if (request.getScheduledPlaces() != null && !request.getScheduledPlaces().isEmpty()) {
            validateScheduledPlaces(request.getScheduledPlaces());
        }
        
        // Remove existing scheduled places
        scheduledPlaceRepository.deleteAll(dailyItinerary.getScheduledPlaces());
        dailyItinerary.getScheduledPlaces().clear();
        
        // Add new scheduled places
        if (request.getScheduledPlaces() != null && !request.getScheduledPlaces().isEmpty()) {
            for (ScheduledPlaceDTO placeDto : request.getScheduledPlaces()) {
                ScheduledPlace scheduledPlace = new ScheduledPlace();
                scheduledPlace.setPlaceId(placeDto.getPlaceId());
                scheduledPlace.setStartTime(Time.valueOf(placeDto.getStartTime()));
                scheduledPlace.setEndTime(Time.valueOf(placeDto.getEndTime()));
                scheduledPlace.setNotes(placeDto.getNotes());
                scheduledPlace.setDailyItinerary(dailyItinerary);
                
                // Set the date from the daily itinerary
                scheduledPlace.setDate(dailyItinerary.getDate());
                
                scheduledPlaceRepository.save(scheduledPlace);
            }
        }
        
        // Refresh the itinerary to include the updated scheduled places
        DailyItinerary refreshedItinerary = dailyItineraryRepository.findById(dailyItinerary.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated itinerary"));
        
        // Sort scheduled places by start time
        if (refreshedItinerary.getScheduledPlaces() != null) {
            refreshedItinerary.getScheduledPlaces().sort(Comparator.comparing(ScheduledPlace::getStartTime));
        }
        
        return refreshedItinerary;
    }
    
    @Transactional
    public void deleteItinerary(String tripId, Integer day, String userEmail) {
        // Verify user owns the trip
        Trip trip = getTripByIdAndUserEmail(tripId, userEmail);
        
        // Find the daily itinerary for the specified day
        DailyItinerary dailyItinerary = trip.getDailyItineraries().stream()
                .filter(itinerary -> itinerary.getDay().equals(day))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "No itinerary found for day " + day));
        
        // First, delete all scheduled places associated with this itinerary
        if (dailyItinerary.getScheduledPlaces() != null && !dailyItinerary.getScheduledPlaces().isEmpty()) {
            scheduledPlaceRepository.deleteAll(dailyItinerary.getScheduledPlaces());
            dailyItinerary.getScheduledPlaces().clear();
        }
        
        // Remove the daily itinerary from the trip's collection
        trip.getDailyItineraries().remove(dailyItinerary);
        
        // Delete the daily itinerary
        dailyItineraryRepository.delete(dailyItinerary);
    }
    
    /**
     * Validates a list of scheduled places to ensure:
     * 1. For each place, start time is before end time
     * 2. No time overlaps between different scheduled places
     * 
     * @param scheduledPlaces List of scheduled places to validate
     * @throws ResponseStatusException if validation fails
     */
    private void validateScheduledPlaces(List<ScheduledPlaceDTO> scheduledPlaces) {
        // Validate that start time is before end time for each place
        for (ScheduledPlaceDTO place : scheduledPlaces) {
            if (place.getStartTime() == null || place.getEndTime() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Start time and end time must be provided for each place");
            }
            
            if (place.getStartTime().isAfter(place.getEndTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Start time must be before end time for place " + place.getPlaceId());
            }
        }
        
        // Sort scheduled places by start time to make overlap checking easier
        List<ScheduledPlaceDTO> sortedPlaces = new ArrayList<>(scheduledPlaces);
        sortedPlaces.sort(Comparator.comparing(ScheduledPlaceDTO::getStartTime));
        
        // Check for time overlaps between different scheduled places
        for (int i = 0; i < sortedPlaces.size() - 1; i++) {
            ScheduledPlaceDTO current = sortedPlaces.get(i);
            ScheduledPlaceDTO next = sortedPlaces.get(i + 1);
            
            // If the end time of the current place is after the start time of the next place, there's an overlap
            if (current.getEndTime().isAfter(next.getStartTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                        "Time overlap detected between places " + current.getPlaceId() + 
                        " and " + next.getPlaceId());
            }
        }
    }
    
    private Trip getTripByIdAndUserEmail(String tripId, String userEmail) {
        // Find the user
        AppUser user = appUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Find the trip and verify ownership
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
        
        if (!trip.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have access to this trip");
        }
        
        return trip;
    }
} 