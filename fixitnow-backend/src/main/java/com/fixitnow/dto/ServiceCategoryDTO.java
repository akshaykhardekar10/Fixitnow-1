package com.fixitnow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCategoryDTO {
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @Size(max = 255)
    private String iconUrl;
    
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
