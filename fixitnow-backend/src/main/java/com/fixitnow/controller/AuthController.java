package com.fixitnow.controller;

import com.fixitnow.dto.AuthRequest;
import com.fixitnow.dto.AuthResponse;
import com.fixitnow.dto.UserDTO;
import com.fixitnow.model.Role;
import com.fixitnow.service.JwtService;
import com.fixitnow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // keep for development; tighten in production
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password required"));
        }
        if (userService.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        
        UserDTO userDTO = UserDTO.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(req.getPassword())
                .location(req.getLocation())
                .build();
                
        try {
            if (req.getRole() != null) {
                userDTO.setRole(Role.valueOf(req.getRole().toUpperCase()));
            } else {
                userDTO.setRole(Role.CUSTOMER);
            }
        } catch (Exception ex) {
            userDTO.setRole(Role.CUSTOMER);
        }
        
        UserDTO savedUser = userService.createUser(userDTO);

        // Return some useful info (no password)
        return ResponseEntity.ok(Map.of(
                "message", "User registered",
                "email", savedUser.getEmail(),
                "role", savedUser.getRole().name()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        // load UserDetails (for token generation) and also fetch User entity to get role
        UserDetails ud = userDetailsService.loadUserByUsername(req.getEmail());

        // fetch full user record to include role and optional other fields
        UserDTO currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            // Try to find by email directly
            var userOpt = userService.findAllUsers().stream()
                    .filter(u -> u.getEmail().equals(req.getEmail()))
                    .findFirst();
            if (userOpt.isEmpty()) {
                // unexpected, but handle gracefully
                String accessFallback = jwtService.generateAccessToken(ud);
                String refreshFallback = jwtService.generateRefreshToken(ud);
                return ResponseEntity.ok(new AuthResponse(accessFallback, refreshFallback, null));
            }
            currentUser = userOpt.get();
        }

        String access = jwtService.generateAccessToken(ud);
        String refresh = jwtService.generateRefreshToken(ud);

        // return access, refresh and role
        AuthResponse response = new AuthResponse(access, refresh, currentUser.getRole() != null ? currentUser.getRole().name() : null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) return ResponseEntity.status(400).body(Map.of("error", "refreshToken required"));
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail == null) return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        UserDetails ud = userDetailsService.loadUserByUsername(userEmail);
        if (!jwtService.isTokenValid(refreshToken, ud))
            return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));
        String newAccess = jwtService.generateAccessToken(ud);
        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        
        UserDTO currentUser = userService.getCurrentUser();
        if (currentUser == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        
        return ResponseEntity.ok(Map.of(
                "id", currentUser.getId(),
                "email", currentUser.getEmail(),
                "name", currentUser.getName(),
                "role", currentUser.getRole(),
                "location", currentUser.getLocation()
        ));
    }
}
