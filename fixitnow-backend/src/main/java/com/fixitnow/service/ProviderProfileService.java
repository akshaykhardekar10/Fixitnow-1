package com.fixitnow.service;

import com.fixitnow.dto.ProviderProfileDTO;

import java.util.List;
import java.util.Optional;

public interface ProviderProfileService {
    List<ProviderProfileDTO> findAllProviders();
    List<ProviderProfileDTO> findAvailableProviders();
    List<ProviderProfileDTO> findVerifiedProviders();
    List<ProviderProfileDTO> findProvidersByCategory(Long categoryId);
    List<ProviderProfileDTO> findProvidersByLocation(String location);
    Optional<ProviderProfileDTO> findProviderById(Long id);
    Optional<ProviderProfileDTO> findProviderByUserId(Long userId);
    ProviderProfileDTO createOrUpdateProfile(Long userId, ProviderProfileDTO profileDTO);
    void deleteProfile(Long id);
    ProviderProfileDTO updateAvailability(Long userId, boolean available);
}
