package com.triptide.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    // GET request: /api/greet?name=John
    @GetMapping("/greet")
    public String greet(@RequestParam(defaultValue = "Kenneth") String name) {
        return String.format("Hello, %s!", name);
    }

    // POST request: /api/message
    @PostMapping("/message")
    public String createMessage(@RequestBody String message) {
        return "Received message: " + message;
    }

    // GET request: /api/users/{id}
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id) {
        return "User ID: " + id;
    }
} 