package com.triptide.backend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.triptide.backend.model.Lodging;

public interface LodgingRepository extends JpaRepository<Lodging, Long> {
    
    Optional<Lodging> findByPlaceId(String placeId);
    Page<Lodging> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<Lodging> findByTagsName(String tag, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM Lodging t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<Lodging> findByTagsNameIn(@Param("tagNames") Set<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Lodging t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<Lodging> findByNameContainingIgnoreCaseAndTagsNameIn(
        @Param("name") String name, 
        @Param("tagNames") Set<String> tagNames, 
        Pageable pageable
    );

} 