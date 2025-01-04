package com.triptide.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.TouristAttraction;

public interface TouristAttractionRepository extends JpaRepository<TouristAttraction, Long> {
} 