package com.fixitnow.controller;

import com.fixitnow.dto.UserDTO;
import com.fixitnow.model.Role;
import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/promote")
    public ResponseEntity<?> promoteUserToAdmin(@PathVariable Long userId) {
        try {
            UserDTO user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (user.getRole() == Role.ADMIN) {
                return ResponseEntity.badRequest().body(Map.of("error", "User is already an admin"));
            }
            
            user.setRole(Role.ADMIN);
            UserDTO updatedUser = userService.updateUser(userId, user);
            
            return ResponseEntity.ok(Map.of(
                    "message", "User promoted to admin successfully",
                    "userId", updatedUser.getId(),
                    "email", updatedUser.getEmail(),
                    "newRole", updatedUser.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/demote")
    public ResponseEntity<?> demoteAdminToCustomer(@PathVariable Long userId) {
        try {
            UserDTO user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (user.getRole() != Role.ADMIN) {
                return ResponseEntity.badRequest().body(Map.of("error", "User is not an admin"));
            }
            
            user.setRole(Role.CUSTOMER);
            UserDTO updatedUser = userService.updateUser(userId, user);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Admin demoted to customer successfully",
                    "userId", updatedUser.getId(),
                    "email", updatedUser.getEmail(),
                    "newRole", updatedUser.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String newRoleStr = request.get("role");
            if (newRoleStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role is required"));
            }
            
            Role newRole;
            try {
                newRole = Role.valueOf(newRoleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Must be CUSTOMER, PROVIDER, or ADMIN"));
            }
            
            UserDTO user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            user.setRole(newRole);
            UserDTO updatedUser = userService.updateUser(userId, user);
            
            return ResponseEntity.ok(Map.of(
                    "message", "User role updated successfully",
                    "userId", updatedUser.getId(),
                    "email", updatedUser.getEmail(),
                    "oldRole", user.getRole().name(),
                    "newRole", updatedUser.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            UserDTO user = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            userService.deleteUser(userId);
            
            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully",
                    "deletedUserId", userId,
                    "deletedUserEmail", user.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/stats")
    public ResponseEntity<?> getUserStats() {
        List<UserDTO> allUsers = userService.findAllUsers();
        
        long customerCount = allUsers.stream().filter(u -> u.getRole() == Role.CUSTOMER).count();
        long providerCount = allUsers.stream().filter(u -> u.getRole() == Role.PROVIDER).count();
        long adminCount = allUsers.stream().filter(u -> u.getRole() == Role.ADMIN).count();
        
        return ResponseEntity.ok(Map.of(
                "totalUsers", allUsers.size(),
                "customers", customerCount,
                "providers", providerCount,
                "admins", adminCount
        ));
    }
}
