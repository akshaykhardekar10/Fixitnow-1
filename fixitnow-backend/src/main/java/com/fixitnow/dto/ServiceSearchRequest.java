package com.fixitnow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSearchRequest {
    
    private String keyword;
    
    private Long categoryId;
    
    private Long subcategoryId;
    
    private String location;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private BigDecimal minRating;
    
    private Boolean verifiedOnly;
    
    private String sortBy; // price, rating, recent, popular
    
    private String sortOrder; // asc, desc
    
    @Builder.Default
    private Integer page = 0;
    
    @Builder.Default
    private Integer size = 20;
}
