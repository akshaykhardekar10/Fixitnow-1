package com.fixitnow.service;

import com.fixitnow.dto.ReviewDTO;
import com.fixitnow.exception.ResourceNotFoundException;
import com.fixitnow.model.ProviderProfile;
import com.fixitnow.model.Review;
import com.fixitnow.model.ServiceListing;
import com.fixitnow.model.User;
import com.fixitnow.repository.ProviderProfileRepository;
import com.fixitnow.repository.ReviewRepository;
import com.fixitnow.repository.ServiceListingRepository;
import com.fixitnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ServiceListingRepository serviceListingRepository;
    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> findReviewsByService(Long serviceListingId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewRepository.findByServiceListingId(serviceListingId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> findReviewsByProvider(Long providerProfileId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewRepository.findByProviderProfileId(providerProfileId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> findReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO, Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        ServiceListing serviceListing = serviceListingRepository.findById(reviewDTO.getServiceListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Service listing not found"));
        
        // Check if customer has already reviewed this service
        if (reviewRepository.existsByServiceListingIdAndCustomerId(reviewDTO.getServiceListingId(), customerId)) {
            throw new IllegalStateException("You have already reviewed this service");
        }
        
        Review review = Review.builder()
                .serviceListing(serviceListing)
                .providerProfile(serviceListing.getProviderProfile())
                .customer(customer)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        // Update provider rating
        updateProviderRating(serviceListing.getProviderProfile().getId());
        
        return toDTO(savedReview);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO, Long customerId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        // Verify ownership
        if (!review.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You don't have permission to update this review");
        }
        
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        Review updatedReview = reviewRepository.save(review);
        
        // Update provider rating
        updateProviderRating(review.getProviderProfile().getId());
        
        return toDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id, Long customerId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        // Verify ownership
        if (!review.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("You don't have permission to delete this review");
        }
        
        Long providerProfileId = review.getProviderProfile().getId();
        reviewRepository.delete(review);
        
        // Update provider rating
        updateProviderRating(providerProfileId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomerReviewedService(Long serviceListingId, Long customerId) {
        return reviewRepository.existsByServiceListingIdAndCustomerId(serviceListingId, customerId);
    }

    private void updateProviderRating(Long providerProfileId) {
        ProviderProfile provider = providerProfileRepository.findById(providerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        
        BigDecimal avgRating = reviewRepository.getAverageRatingForProvider(providerProfileId);
        Integer totalReviews = reviewRepository.getTotalReviewsForProvider(providerProfileId);
        
        provider.setRating(avgRating != null ? avgRating : BigDecimal.ZERO);
        provider.setTotalReviews(totalReviews != null ? totalReviews : 0);
        
        providerProfileRepository.save(provider);
    }

    private ReviewDTO toDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .serviceListingId(review.getServiceListing().getId())
                .serviceTitle(review.getServiceListing().getTitle())
                .providerProfileId(review.getProviderProfile().getId())
                .providerName(review.getProviderProfile().getUser().getName())
                .customerId(review.getCustomer().getId())
                .customerName(review.getCustomer().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
