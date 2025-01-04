package com.triptide.backend.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.TouristAttraction;

public interface TouristAttractionRepository extends JpaRepository<TouristAttraction, Long> {
    List<TouristAttraction> findByTypesContaining(String type, Pageable pageable);
} 