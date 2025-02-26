package com.triptide.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.triptide.backend.model.AppUser;
import com.triptide.backend.model.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUser(AppUser user);
} 