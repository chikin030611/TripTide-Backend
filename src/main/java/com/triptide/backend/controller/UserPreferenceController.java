package com.triptide.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.triptide.backend.dto.UserPreferenceDTO;
import com.triptide.backend.security.JwtService;
import com.triptide.backend.service.UserPreferenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/api/preferences", "/api/preferences/"})
@RequiredArgsConstructor
public class UserPreferenceController {
    
    private final UserPreferenceService userPreferenceService;
    private final JwtService jwtService;

    @RequestMapping(value = {"", "/"}, method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<Void> updatePreferences(@RequestBody UserPreferenceDTO preferences) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Get the username/userId from principal
        userPreferenceService.updateUserPreferences(userId, preferences);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = {"", "/"})
    public ResponseEntity<UserPreferenceDTO> getPreferences() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // Get the username/userId from principal
        return ResponseEntity.ok(userPreferenceService.getUserPreferences(userId));
    }
} 