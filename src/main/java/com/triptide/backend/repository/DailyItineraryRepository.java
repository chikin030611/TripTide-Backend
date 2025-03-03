package com.triptide.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.DailyItinerary;

public interface DailyItineraryRepository extends JpaRepository<DailyItinerary, String> {
    List<DailyItinerary> findByTripId(String tripId);
} 