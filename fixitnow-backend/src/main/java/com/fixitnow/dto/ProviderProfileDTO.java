package com.fixitnow.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfileDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userLocation;
    
    private Long serviceCategoryId;
    private String serviceCategoryName;
    
    @Size(max = 1000)
    private String bio;
    
    @Size(max = 500)
    private String skills;
    
    @Size(max = 500)
    private String serviceArea;
    
    @DecimalMin(value = "0.0", message = "Hourly rate must be positive")
    @DecimalMax(value = "10000.0", message = "Hourly rate must be reasonable")
    private BigDecimal hourlyRate;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal rating;
    
    private Integer totalReviews;
    private boolean available;
    private boolean verified;
    
    @Size(max = 255)
    private String profileImageUrl;
    
    private List<String> certifications;
    
    private Instant createdAt;
    private Instant updatedAt;
}
