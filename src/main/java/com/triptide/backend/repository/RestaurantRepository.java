package com.triptide.backend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.triptide.backend.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByPlaceId(String placeId);
    Page<Restaurant> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Restaurant> findByTagsName(String tag, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Restaurant t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<Restaurant> findByTagsNameIn(@Param("tagNames") Set<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);

    @Query("SELECT DISTINCT t FROM Restaurant t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<Restaurant> findByNameContainingIgnoreCaseAndTagsNameIn(
        @Param("name") String name, 
        @Param("tagNames") Set<String> tagNames, 
        Pageable pageable
    );
} 
