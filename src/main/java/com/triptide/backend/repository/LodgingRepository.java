package com.triptide.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.Lodging;

public interface LodgingRepository extends JpaRepository<Lodging, Long> {
    
    Optional<Lodging> findByPlaceId(String placeId);
    Page<Lodging> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Lodging> findByTagsName(String tag, Pageable pageable);
} 