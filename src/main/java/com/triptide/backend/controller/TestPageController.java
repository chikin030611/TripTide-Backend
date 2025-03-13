package com.triptide.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPageController {
    
    @GetMapping("/test/itinerary")
    public String itineraryTestPage() {
        return "forward:/itinerary-test.html";
    }
} 