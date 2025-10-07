package com.fixitnow.controller;

import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> healthCheck() {
        try {
            // Test database connectivity by getting user count
            long userCount = userService.findAllUsers().size();
            
            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", Instant.now(),
                "application", "FixItNow Backend",
                "version", "1.0.0",
                "database", Map.of(
                    "status", "CONNECTED",
                    "userCount", userCount
                ),
                "features", Map.of(
                    "authentication", "JWT-based",
                    "authorization", "Role-based (CUSTOMER, PROVIDER, ADMIN)",
                    "userManagement", "Full CRUD with admin controls",
                    "locationCapture", "Geolocation with reverse geocoding",
                    "validation", "Bean validation with custom constraints"
                ),
                "endpoints", Map.of(
                    "auth", "/api/auth/* (register, login, refresh, me)",
                    "users", "/api/users/* (profile, providers)",
                    "admin", "/api/admin/* (user management, statistics)",
                    "test", "/api/test/* (development testing)",
                    "validate", "/api/validate/* (field validation)",
                    "health", "/api/health (this endpoint)"
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "DOWN",
                "timestamp", Instant.now(),
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/database")
    public ResponseEntity<?> databaseHealth() {
        try {
            userService.findAllUsers();
            return ResponseEntity.ok(Map.of(
                "database", "HEALTHY",
                "connection", "ACTIVE",
                "timestamp", Instant.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "database", "UNHEALTHY", 
                "error", e.getMessage(),
                "timestamp", Instant.now()
            ));
        }
    }
}
