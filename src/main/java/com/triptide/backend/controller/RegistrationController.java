package com.triptide.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.triptide.backend.model.AppUser;
import com.triptide.backend.model.AppUserRepository;

@RestController
public class RegistrationController {

    @Autowired
    private AppUserRepository appUserRepository;
    
    @PostMapping(value = "/registration", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppUser register(@RequestBody AppUser appUser) {
        return appUserRepository.save(appUser);
    }
}
