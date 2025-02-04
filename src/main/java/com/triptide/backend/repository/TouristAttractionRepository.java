package com.triptide.backend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.triptide.backend.model.TouristAttraction;

public interface TouristAttractionRepository extends JpaRepository<TouristAttraction, Long> {
    Optional<TouristAttraction> findByPlaceId(String placeId);
    Page<TouristAttraction> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<TouristAttraction> findByTagsName(String tagName, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM TouristAttraction t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<TouristAttraction> findByTagsNameIn(@Param("tagNames") Set<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);
} 