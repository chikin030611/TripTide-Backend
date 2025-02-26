package com.triptide.backend.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByTypeAndName(String type, String name);
    Set<Tag> findByType(String type);
    Optional<Tag> findByName(String name);
} 