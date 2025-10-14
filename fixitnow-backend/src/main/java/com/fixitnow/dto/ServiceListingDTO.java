package com.fixitnow.dto;

import com.fixitnow.model.PricingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ServiceListingDTO {
    
    private Long id;
    
    private Long providerProfileId;
    
    private String providerName;
    
    private BigDecimal providerRating;
    
    private Integer providerTotalReviews;
    
    private String providerImageUrl;
    
    @NotNull(message = "Category is required")
    private Long categoryId;
    
    private String categoryName;
    
    private Long subcategoryId;
    
    private String subcategoryName;
    
    @NotBlank(message = "Service title is required")
    @Size(max = 200)
    private String title;
    
    @NotBlank(message = "Service description is required")
    @Size(max = 2000)
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Pricing type is required")
    private PricingType pricingType;
    
    @Size(max = 500)
    private String serviceLocation;
    
    private List<String> imageUrls;
    
    @Size(max = 100)
    private String estimatedDuration;
    
    private List<String> availabilityDays;
    
    private boolean active;
    
    private Integer viewCount;
    
    private Integer bookingCount;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}
