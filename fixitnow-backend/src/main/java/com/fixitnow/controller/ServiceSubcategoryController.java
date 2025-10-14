package com.fixitnow.controller;

import com.fixitnow.dto.ServiceSubcategoryDTO;
import com.fixitnow.service.ServiceSubcategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subcategories")
@RequiredArgsConstructor
public class ServiceSubcategoryController {

    private final ServiceSubcategoryService subcategoryService;

    @GetMapping
    public ResponseEntity<List<ServiceSubcategoryDTO>> getAllSubcategories() {
        return ResponseEntity.ok(subcategoryService.findActiveSubcategories());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ServiceSubcategoryDTO>> getSubcategoriesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(subcategoryService.findSubcategoriesByCategory(categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubcategoryById(@PathVariable Long id) {
        return subcategoryService.findSubcategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSubcategory(@Valid @RequestBody ServiceSubcategoryDTO subcategoryDTO) {
        try {
            ServiceSubcategoryDTO created = subcategoryService.createSubcategory(subcategoryDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Subcategory created successfully",
                    "subcategory", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSubcategory(@PathVariable Long id, @Valid @RequestBody ServiceSubcategoryDTO subcategoryDTO) {
        try {
            ServiceSubcategoryDTO updated = subcategoryService.updateSubcategory(id, subcategoryDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Subcategory updated successfully",
                    "subcategory", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubcategory(@PathVariable Long id) {
        try {
            subcategoryService.deleteSubcategory(id);
            return ResponseEntity.ok(Map.of("message", "Subcategory deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
