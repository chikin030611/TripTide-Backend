package com.triptide.backend.repository;

import com.triptide.backend.model.Lodging;
import com.triptide.backend.model.Restaurant;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LodgingRepository extends JpaRepository<Lodging, Long> {
    
    Optional<Lodging> findByPlaceId(String placeId);
    Page<Lodging> findByNameContainingIgnoreCase(String name, Pageable pageable);
} 