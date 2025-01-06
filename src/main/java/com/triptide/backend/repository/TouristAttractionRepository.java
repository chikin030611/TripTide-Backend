package com.triptide.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.TouristAttraction;

public interface TouristAttractionRepository extends JpaRepository<TouristAttraction, Long> {
    Optional<TouristAttraction> findByPlaceId(String placeId);
    Page<TouristAttraction> findByNameContainingIgnoreCase(String name, Pageable pageable);
} 