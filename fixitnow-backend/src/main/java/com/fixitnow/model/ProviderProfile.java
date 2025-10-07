package com.fixitnow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
@Table(name = "provider_profiles")
public class ProviderProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "service_category_id")
    private ServiceCategory serviceCategory;
    
    @Size(max = 1000)
    private String bio;
    
    @Size(max = 500)
    private String skills; // Comma-separated skills
    
    @Size(max = 500)
    private String serviceArea; // Geographic service area
    
    @DecimalMin(value = "0.0", message = "Hourly rate must be positive")
    @DecimalMax(value = "10000.0", message = "Hourly rate must be reasonable")
    private BigDecimal hourlyRate;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Builder.Default
    private Integer totalReviews = 0;
    
    @Builder.Default
    private boolean available = true;
    
    @Builder.Default
    private boolean verified = false;
    
    @Size(max = 255)
    private String profileImageUrl;
    
    @ElementCollection
    @CollectionTable(name = "provider_certifications", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "certification")
    private List<String> certifications;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    private Instant updatedAt;
}
