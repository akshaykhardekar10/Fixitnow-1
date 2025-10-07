package com.fixitnow.controller;

import com.fixitnow.dto.UserDTO;
import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            UserDTO currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            return ResponseEntity.ok(Map.of(
                    "id", currentUser.getId(),
                    "name", currentUser.getName(),
                    "email", currentUser.getEmail(),
                    "role", currentUser.getRole().name(),
                    "location", currentUser.getLocation(),
                    "enabled", currentUser.isEnabled(),
                    "createdAt", currentUser.getCreatedAt(),
                    "updatedAt", currentUser.getUpdatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody Map<String, String> updates) {
        try {
            UserDTO currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            
            // Update allowed fields
            if (updates.containsKey("name")) {
                currentUser.setName(updates.get("name"));
            }
            if (updates.containsKey("location")) {
                currentUser.setLocation(updates.get("location"));
            }
            
            UserDTO updatedUser = userService.updateUser(currentUser.getId(), currentUser);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "user", Map.of(
                            "id", updatedUser.getId(),
                            "name", updatedUser.getName(),
                            "email", updatedUser.getEmail(),
                            "role", updatedUser.getRole().name(),
                            "location", updatedUser.getLocation(),
                            "updatedAt", updatedUser.getUpdatedAt()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/providers")
    public ResponseEntity<?> getAllProviders() {
        List<UserDTO> allUsers = userService.findAllUsers();
        List<UserDTO> providers = allUsers.stream()
                .filter(user -> "PROVIDER".equals(user.getRole().name()))
                .toList();
        
        return ResponseEntity.ok(Map.of(
                "totalProviders", providers.size(),
                "providers", providers.stream().map(provider -> Map.of(
                        "id", provider.getId(),
                        "name", provider.getName(),
                        "email", provider.getEmail(),
                        "location", provider.getLocation(),
                        "createdAt", provider.getCreatedAt()
                )).toList()
        ));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name(),
                    "location", user.getLocation(),
                    "enabled", user.isEnabled(),
                    "createdAt", user.getCreatedAt(),
                    "updatedAt", user.getUpdatedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
