package com.fixitnow.repository;

import com.fixitnow.model.ProviderProfile;
import com.fixitnow.model.ServiceCategory;
import com.fixitnow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {
    Optional<ProviderProfile> findByUser(User user);
    Optional<ProviderProfile> findByUserId(Long userId);
    List<ProviderProfile> findByServiceCategory(ServiceCategory serviceCategory);
    List<ProviderProfile> findByAvailableTrue();
    List<ProviderProfile> findByVerifiedTrue();
    
    @Query("SELECT p FROM ProviderProfile p WHERE p.serviceArea LIKE %:location%")
    List<ProviderProfile> findByServiceAreaContaining(@Param("location") String location);
    
    @Query("SELECT p FROM ProviderProfile p WHERE p.available = true AND p.serviceCategory.id = :categoryId")
    List<ProviderProfile> findAvailableProvidersByCategory(@Param("categoryId") Long categoryId);
}
