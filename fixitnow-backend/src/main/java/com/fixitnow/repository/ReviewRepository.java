package com.fixitnow.repository;

import com.fixitnow.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByServiceListingId(Long serviceListingId, Pageable pageable);
    
    Page<Review> findByProviderProfileId(Long providerProfileId, Pageable pageable);
    
    List<Review> findByCustomerId(Long customerId);
    
    Optional<Review> findByServiceListingIdAndCustomerId(Long serviceListingId, Long customerId);
    
    boolean existsByServiceListingIdAndCustomerId(Long serviceListingId, Long customerId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.serviceListing.id = :serviceListingId")
    BigDecimal getAverageRatingForService(@Param("serviceListingId") Long serviceListingId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.providerProfile.id = :providerProfileId")
    BigDecimal getAverageRatingForProvider(@Param("providerProfileId") Long providerProfileId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.providerProfile.id = :providerProfileId")
    Integer getTotalReviewsForProvider(@Param("providerProfileId") Long providerProfileId);
    
    long countByServiceListingId(Long serviceListingId);
}
