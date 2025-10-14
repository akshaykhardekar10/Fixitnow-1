package com.fixitnow.controller;

import com.fixitnow.dto.ProviderProfileDTO;
import com.fixitnow.service.ProviderProfileService;
import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderProfileService providerService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ProviderProfileDTO>> getAllProviders() {
        List<ProviderProfileDTO> providers = providerService.findAvailableProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/verified")
    public ResponseEntity<List<ProviderProfileDTO>> getVerifiedProviders() {
        List<ProviderProfileDTO> providers = providerService.findVerifiedProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProviderProfileDTO>> getProvidersByCategory(@PathVariable Long categoryId) {
        List<ProviderProfileDTO> providers = providerService.findProvidersByCategory(categoryId);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/location")
    public ResponseEntity<List<ProviderProfileDTO>> getProvidersByLocation(@RequestParam String location) {
        List<ProviderProfileDTO> providers = providerService.findProvidersByLocation(location);
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProviderById(@PathVariable Long id) {
        return providerService.findProviderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentProviderProfile() {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            return providerService.findProviderByUserId(currentUser.getId())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> createOrUpdateProviderProfile(@Valid @RequestBody ProviderProfileDTO profileDTO) {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            ProviderProfileDTO savedProfile = providerService.createOrUpdateProfile(currentUser.getId(), profileDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Provider profile saved successfully",
                    "profile", savedProfile
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProviderProfile(@Valid @RequestBody ProviderProfileDTO profileDTO) {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            ProviderProfileDTO updatedProfile = providerService.createOrUpdateProfile(currentUser.getId(), profileDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Provider profile updated successfully",
                    "profile", updatedProfile
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/availability")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateAvailability(@RequestBody Map<String, Boolean> request) {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            Boolean available = request.get("available");
            if (available == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Available status is required"));
            }

            ProviderProfileDTO updatedProfile = providerService.updateAvailability(currentUser.getId(), available);
            return ResponseEntity.ok(Map.of(
                    "message", "Availability updated successfully",
                    "available", updatedProfile.isAvailable()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('PROVIDER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProviderProfile() {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            var profile = providerService.findProviderByUserId(currentUser.getId());
            if (profile.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            providerService.deleteProfile(profile.get().getId());
            return ResponseEntity.ok(Map.of("message", "Provider profile deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
