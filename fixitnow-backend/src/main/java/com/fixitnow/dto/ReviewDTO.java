package com.fixitnow.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    
    private Long id;
    
    @NotNull(message = "Service listing ID is required")
    private Long serviceListingId;
    
    private String serviceTitle;
    
    private Long providerProfileId;
    
    private String providerName;
    
    private Long customerId;
    
    private String customerName;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    @Size(max = 1000)
    private String comment;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}
