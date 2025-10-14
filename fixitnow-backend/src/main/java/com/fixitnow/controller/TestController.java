package com.fixitnow.controller;

import com.fixitnow.dto.UserDTO;
import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsersWithDetails() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(Map.of(
                "totalUsers", users.size(),
                "users", users
        ));
    }

    @GetMapping("/users/by-role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<UserDTO> allUsers = userService.findAllUsers();
            List<UserDTO> filteredUsers = allUsers.stream()
                    .filter(user -> user.getRole().name().equalsIgnoreCase(role))
                    .toList();
            
            return ResponseEntity.ok(Map.of(
                    "role", role.toUpperCase(),
                    "count", filteredUsers.size(),
                    "users", filteredUsers
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/with-location")
    public ResponseEntity<?> getUsersWithLocation() {
        List<UserDTO> allUsers = userService.findAllUsers();
        List<UserDTO> usersWithLocation = allUsers.stream()
                .filter(user -> user.getLocation() != null && !user.getLocation().trim().isEmpty())
                .toList();
        
        return ResponseEntity.ok(Map.of(
                "totalUsers", allUsers.size(),
                "usersWithLocation", usersWithLocation.size(),
                "users", usersWithLocation
        ));
    }

    @GetMapping("/database-schema-info")
    public ResponseEntity<?> getDatabaseSchemaInfo() {
        return ResponseEntity.ok(Map.of(
                "message", "Database schema should include the following columns in 'users' table:",
                "columns", List.of(
                        "id (BIGINT, PRIMARY KEY, AUTO_INCREMENT)",
                        "name (VARCHAR(100), NOT NULL)",
                        "email (VARCHAR(100), UNIQUE, NOT NULL)",
                        "password (VARCHAR(255), NOT NULL)",
                        "role (VARCHAR(255), NOT NULL)", 
                        "location (VARCHAR(200))",
                        "enabled (BOOLEAN, DEFAULT TRUE)",
                        "created_at (TIMESTAMP)",
                        "updated_at (TIMESTAMP)"
                ),
                "note", "With spring.jpa.hibernate.ddl-auto=update, these columns will be created automatically"
        ));
    }
}
