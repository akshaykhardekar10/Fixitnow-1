package com.fixitnow.service;

import com.fixitnow.dto.ReviewDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    
    Page<ReviewDTO> findReviewsByService(Long serviceListingId, int page, int size);
    
    Page<ReviewDTO> findReviewsByProvider(Long providerProfileId, int page, int size);
    
    List<ReviewDTO> findReviewsByCustomer(Long customerId);
    
    ReviewDTO createReview(ReviewDTO reviewDTO, Long customerId);
    
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO, Long customerId);
    
    void deleteReview(Long id, Long customerId);
    
    boolean hasCustomerReviewedService(Long serviceListingId, Long customerId);
}
