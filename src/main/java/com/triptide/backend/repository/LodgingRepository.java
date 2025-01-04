package com.triptide.backend.repository;

import com.triptide.backend.model.Lodging;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LodgingRepository extends JpaRepository<Lodging, Long> {
} 