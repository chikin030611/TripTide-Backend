package com.triptide.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.ScheduledPlace;

public interface ScheduledPlaceRepository extends JpaRepository<ScheduledPlace, String> {
    List<ScheduledPlace> findByDailyItineraryId(String dailyItineraryId);
} 