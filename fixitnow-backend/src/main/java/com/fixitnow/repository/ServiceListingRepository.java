package com.fixitnow.repository;

import com.fixitnow.model.ServiceListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ServiceListingRepository extends JpaRepository<ServiceListing, Long>, JpaSpecificationExecutor<ServiceListing> {
    
    List<ServiceListing> findByProviderProfileIdAndActiveTrue(Long providerProfileId);
    
    List<ServiceListing> findByProviderProfileId(Long providerProfileId);
    
    Page<ServiceListing> findByActiveTrue(Pageable pageable);
    
    Page<ServiceListing> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    
    Page<ServiceListing> findBySubcategoryIdAndActiveTrue(Long subcategoryId, Pageable pageable);
    
    @Query("SELECT s FROM ServiceListing s WHERE s.active = true " +
           "AND (:categoryId IS NULL OR s.category.id = :categoryId) " +
           "AND (:subcategoryId IS NULL OR s.subcategory.id = :subcategoryId) " +
           "AND (:location IS NULL OR LOWER(s.serviceLocation) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR s.price <= :maxPrice)")
    Page<ServiceListing> searchServices(
        @Param("categoryId") Long categoryId,
        @Param("subcategoryId") Long subcategoryId,
        @Param("location") String location,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
    
    @Query("SELECT s FROM ServiceListing s WHERE s.active = true " +
           "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ServiceListing> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    long countByProviderProfileId(Long providerProfileId);
}
