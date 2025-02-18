package com.triptide.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.triptide.backend.model.TouristAttraction;

@Repository
public interface TouristAttractionRepository extends JpaRepository<TouristAttraction, Long> {
    Optional<TouristAttraction> findByPlaceId(String placeId);
    Page<TouristAttraction> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<TouristAttraction> findByTagsName(String tagName, Pageable pageable);
    
    @Query("SELECT DISTINCT t FROM TouristAttraction t JOIN t.tags tag WHERE tag.name IN :tagNames")
    Page<TouristAttraction> findByTagsNameIn(@Param("tagNames") Set<String> tagNames, @Param("tagCount") long tagCount, Pageable pageable);

    @Query("SELECT DISTINCT t FROM TouristAttraction t JOIN t.tags tag " +
           "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND tag.name IN :tagNames")
    Page<TouristAttraction> findByNameContainingIgnoreCaseAndTagsNameIn(
        @Param("name") String name, 
        @Param("tagNames") Set<String> tagNames, 
        Pageable pageable
    );

    @Query("SELECT DISTINCT ta, COUNT(t) as tagCount FROM TouristAttraction ta JOIN ta.tags t " +
           "WHERE t.name IN :tagNames GROUP BY ta ORDER BY tagCount DESC")
    List<TouristAttraction> findByTags(@Param("tagNames") Set<String> tagNames);

    @Query(value = "SELECT * FROM tourist_attractions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<TouristAttraction> findRandomAttractions(@Param("limit") int limit);
} 