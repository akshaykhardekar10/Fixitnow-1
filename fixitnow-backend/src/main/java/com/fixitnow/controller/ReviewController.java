package com.fixitnow.controller;

import com.fixitnow.dto.ReviewDTO;
import com.fixitnow.service.ReviewService;
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
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Public endpoints - view reviews
    @GetMapping("/service/{serviceListingId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByService(
            @PathVariable Long serviceListingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.findReviewsByService(serviceListingId, page, size));
    }

    @GetMapping("/provider/{providerProfileId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByProvider(
            @PathVariable Long providerProfileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.findReviewsByProvider(providerProfileId, page, size));
    }

    // Customer endpoints - manage their reviews
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<ReviewDTO>> getMyReviews(Authentication authentication) {
        Long customerId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(reviewService.findReviewsByCustomer(customerId));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createReview(
            @Valid @RequestBody ReviewDTO reviewDTO,
            Authentication authentication) {
        try {
            Long customerId = Long.parseLong(authentication.getName());
            ReviewDTO created = reviewService.createReview(reviewDTO, customerId);
            return ResponseEntity.ok(Map.of(
                    "message", "Review submitted successfully",
                    "review", created
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewDTO reviewDTO,
            Authentication authentication) {
        try {
            Long customerId = Long.parseLong(authentication.getName());
            ReviewDTO updated = reviewService.updateReview(id, reviewDTO, customerId);
            return ResponseEntity.ok(Map.of(
                    "message", "Review updated successfully",
                    "review", updated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long customerId = Long.parseLong(authentication.getName());
            reviewService.deleteReview(id, customerId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check/{serviceListingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Boolean>> checkIfReviewed(
            @PathVariable Long serviceListingId,
            Authentication authentication) {
        Long customerId = Long.parseLong(authentication.getName());
        boolean hasReviewed = reviewService.hasCustomerReviewedService(serviceListingId, customerId);
        return ResponseEntity.ok(Map.of("hasReviewed", hasReviewed));
    }
}
