package com.triptide.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.AppUser;
import com.triptide.backend.model.Trip;

public interface TripRepository extends JpaRepository<Trip, String> {
    List<Trip> findByUser(AppUser user);
} 