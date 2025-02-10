package com.triptide.backend.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.triptide.backend.dto.UserPreferenceDTO;
import com.triptide.backend.model.AppUser;
import com.triptide.backend.model.Tag;
import com.triptide.backend.model.UserPreference;
import com.triptide.backend.repository.AppUserRepository;
import com.triptide.backend.repository.TagRepository;
import com.triptide.backend.repository.UserPreferenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {
    
    private final UserPreferenceRepository userPreferenceRepository;
    private final AppUserRepository appUserRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void updateUserPreferences(String userEmail, UserPreferenceDTO preferenceDTO) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Tag> tags = preferenceDTO.getPreferredTags().stream()
            .map(tagName -> tagRepository.findByName(tagName)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + tagName)))
            .collect(Collectors.toSet());

        UserPreference preference = userPreferenceRepository.findByUser(user)
            .orElse(UserPreference.builder().user(user).build());
            
        preference.setPreferredTags(tags);
        userPreferenceRepository.save(preference);
    }

    public UserPreferenceDTO getUserPreferences(String userEmail) {
        AppUser user = appUserRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return userPreferenceRepository.findByUser(user)
            .map(preference -> UserPreferenceDTO.builder()
                .preferredTags(preference.getPreferredTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toSet()))
                .build())
            .orElse(new UserPreferenceDTO(Set.of()));
    }
} 