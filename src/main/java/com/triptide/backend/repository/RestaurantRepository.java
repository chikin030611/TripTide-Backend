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

    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.tags tag WHERE tag.name IN :tagNames GROUP BY r HAVING COUNT(DISTINCT tag.name) = :tagCount")
    Page<Restaurant> findByTagsNameIn(@Param("tagNames") Set<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);
} 