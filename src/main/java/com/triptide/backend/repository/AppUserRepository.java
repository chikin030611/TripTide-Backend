package com.triptide.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.triptide.backend.model.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM AppUser u WHERE u.id = :id")
    Optional<AppUser> findById(@Param("id") Long id);

}
