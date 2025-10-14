package com.fixitnow.controller;

import com.fixitnow.dto.ServiceListingDTO;
import com.fixitnow.dto.ServiceSearchRequest;
import com.fixitnow.service.ServiceListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceListingController {

    private final ServiceListingService serviceListingService;

    // Public endpoints - browse services
    @GetMapping
    public ResponseEntity<Page<ServiceListingDTO>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(serviceListingService.findAllActiveServices(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable Long id) {
        serviceListingService.incrementViewCount(id); // Track view
        return serviceListingService.findServiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ServiceListingDTO>> getServicesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(serviceListingService.findServicesByCategory(categoryId, page, size));
    }

    @GetMapping("/subcategory/{subcategoryId}")
    public ResponseEntity<Page<ServiceListingDTO>> getServicesBySubcategory(
            @PathVariable Long subcategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(serviceListingService.findServicesBySubcategory(subcategoryId, page, size));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<ServiceListingDTO>> searchServices(@RequestBody ServiceSearchRequest searchRequest) {
        return ResponseEntity.ok(serviceListingService.searchServices(searchRequest));
    }

    @GetMapping("/provider/{providerProfileId}")
    public ResponseEntity<List<ServiceListingDTO>> getServicesByProvider(@PathVariable Long providerProfileId) {
        return ResponseEntity.ok(serviceListingService.findServicesByProvider(providerProfileId));
    }

    // Provider endpoints - manage their services
    @PostMapping
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> createService(
            @Valid @RequestBody ServiceListingDTO serviceDTO,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            ServiceListingDTO created = serviceListingService.createService(serviceDTO, userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Service created successfully",
                    "service", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceListingDTO serviceDTO,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            ServiceListingDTO updated = serviceListingService.updateService(id, serviceDTO, userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Service updated successfully",
                    "service", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<?> deleteService(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long userId = Long.parseLong(authentication.getName());
            serviceListingService.deleteService(id, userId);
            return ResponseEntity.ok(Map.of("message", "Service deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
