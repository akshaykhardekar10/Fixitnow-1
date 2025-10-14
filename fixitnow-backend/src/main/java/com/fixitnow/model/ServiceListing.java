package com.fixitnow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_listings")
public class ServiceListing {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "provider_profile_id", nullable = false)
    @NotNull
    private ProviderProfile providerProfile;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private ServiceCategory category;
    
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private ServiceSubcategory subcategory;
    
    @NotBlank(message = "Service title is required")
    @Size(max = 200)
    private String title;
    
    @NotBlank(message = "Service description is required")
    @Size(max = 2000)
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private PricingType pricingType; // HOURLY, FIXED, PER_DAY
    
    @Size(max = 500)
    private String serviceLocation; // Where service is offered
    
    @ElementCollection
    @CollectionTable(name = "service_images", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;
    
    @Size(max = 100)
    private String estimatedDuration; // e.g., "2-3 hours", "1 day"
    
    @ElementCollection
    @CollectionTable(name = "service_availability_days", joinColumns = @JoinColumn(name = "service_id"))
    @Column(name = "day")
    private List<String> availabilityDays; // Monday, Tuesday, etc.
    
    @Builder.Default
    private boolean active = true;
    
    @Builder.Default
    private Integer viewCount = 0;
    
    @Builder.Default
    private Integer bookingCount = 0;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    private Instant updatedAt;
}
