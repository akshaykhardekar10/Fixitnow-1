package com.fixitnow.controller;

import com.fixitnow.dto.AuthRequest;
import com.fixitnow.dto.UserDTO;
import com.fixitnow.model.Role;
import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/validate")
@RequiredArgsConstructor
public class ValidationController {

    private final UserService userService;

    @PostMapping("/registration-flow")
    public ResponseEntity<?> validateRegistrationFlow(@RequestBody AuthRequest testData) {
        try {
            // Simulate the exact frontend registration flow
            if (testData.getEmail() == null || testData.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "error", "Email and password are required"
                ));
            }

            if (userService.existsByEmail(testData.getEmail())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED", 
                    "error", "Email already registered"
                ));
            }

            // Create user exactly like AuthController does
            UserDTO userDTO = UserDTO.builder()
                    .name(testData.getName())
                    .email(testData.getEmail())
                    .password(testData.getPassword())
                    .location(testData.getLocation())
                    .build();

            // Handle role assignment
            try {
                if (testData.getRole() != null) {
                    userDTO.setRole(Role.valueOf(testData.getRole().toUpperCase()));
                } else {
                    userDTO.setRole(Role.CUSTOMER);
                }
            } catch (Exception ex) {
                userDTO.setRole(Role.CUSTOMER);
            }

            UserDTO savedUser = userService.createUser(userDTO);

            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Registration flow validated successfully",
                "user", Map.of(
                    "id", savedUser.getId(),
                    "name", savedUser.getName(),
                    "email", savedUser.getEmail(),
                    "role", savedUser.getRole().name(),
                    "location", savedUser.getLocation() != null ? savedUser.getLocation() : "Not provided",
                    "enabled", savedUser.isEnabled(),
                    "createdAt", savedUser.getCreatedAt()
                ),
                "fieldsValidation", Map.of(
                    "nameStored", savedUser.getName() != null,
                    "emailStored", savedUser.getEmail() != null,
                    "roleStored", savedUser.getRole() != null,
                    "locationStored", savedUser.getLocation() != null,
                    "timestampCreated", savedUser.getCreatedAt() != null
                )
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "FAILED",
                "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/field-mapping")
    public ResponseEntity<?> validateFieldMapping() {
        return ResponseEntity.ok(Map.of(
            "frontendFields", Map.of(
                "name", "Full Name input field",
                "email", "Email Address input field", 
                "password", "Password input field",
                "role", "CUSTOMER/PROVIDER dropdown (defaults to CUSTOMER)",
                "location", "Captured via geolocation API with reverse geocoding"
            ),
            "backendMapping", Map.of(
                "name", "UserDTO.name -> User.name -> users.name (VARCHAR(100))",
                "email", "UserDTO.email -> User.email -> users.email (VARCHAR(100), UNIQUE)",
                "password", "UserDTO.password -> encoded -> User.password -> users.password (VARCHAR(255))",
                "role", "UserDTO.role -> User.role -> users.role (VARCHAR(255), ENUM)",
                "location", "UserDTO.location -> User.location -> users.location (VARCHAR(200))"
            ),
            "additionalFields", Map.of(
                "enabled", "Auto-set to true on registration",
                "createdAt", "Auto-generated timestamp via @CreationTimestamp",
                "updatedAt", "Auto-updated timestamp via @UpdateTimestamp"
            )
        ));
    }

    @PostMapping("/test-user-creation")
    public ResponseEntity<?> createTestUsers() {
        try {
            // Create test customer with location
            UserDTO customer = UserDTO.builder()
                    .name("Test Customer")
                    .email("customer@test.com")
                    .password("test123")
                    .role(Role.CUSTOMER)
                    .location("New York, NY, USA")
                    .build();

            // Create test provider with location  
            UserDTO provider = UserDTO.builder()
                    .name("Test Provider")
                    .email("provider@test.com")
                    .password("test123")
                    .role(Role.PROVIDER)
                    .location("Los Angeles, CA, USA")
                    .build();

            UserDTO savedCustomer = null;
            UserDTO savedProvider = null;

            if (!userService.existsByEmail(customer.getEmail())) {
                savedCustomer = userService.createUser(customer);
            }

            if (!userService.existsByEmail(provider.getEmail())) {
                savedProvider = userService.createUser(provider);
            }

            return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Test users created successfully",
                "customer", savedCustomer != null ? Map.of(
                    "id", savedCustomer.getId(),
                    "name", savedCustomer.getName(),
                    "email", savedCustomer.getEmail(),
                    "role", savedCustomer.getRole().name(),
                    "location", savedCustomer.getLocation()
                ) : "Already exists",
                "provider", savedProvider != null ? Map.of(
                    "id", savedProvider.getId(),
                    "name", savedProvider.getName(),
                    "email", savedProvider.getEmail(),
                    "role", savedProvider.getRole().name(),
                    "location", savedProvider.getLocation()
                ) : "Already exists"
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "FAILED",
                "error", e.getMessage()
            ));
        }
    }
}
