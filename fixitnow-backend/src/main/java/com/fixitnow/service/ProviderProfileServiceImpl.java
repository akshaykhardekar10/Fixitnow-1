package com.fixitnow.service;

import com.fixitnow.dto.ProviderProfileDTO;
import com.fixitnow.exception.ResourceNotFoundException;
import com.fixitnow.model.ProviderProfile;
import com.fixitnow.model.ServiceCategory;
import com.fixitnow.model.User;
import com.fixitnow.repository.ProviderProfileRepository;
import com.fixitnow.repository.ServiceCategoryRepository;
import com.fixitnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderProfileServiceImpl implements ProviderProfileService {

    private final ProviderProfileRepository providerRepository;
    private final UserRepository userRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProviderProfileDTO> findAllProviders() {
        return providerRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderProfileDTO> findAvailableProviders() {
        return providerRepository.findByAvailableTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderProfileDTO> findVerifiedProviders() {
        return providerRepository.findByVerifiedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderProfileDTO> findProvidersByCategory(Long categoryId) {
        return providerRepository.findAvailableProvidersByCategory(categoryId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderProfileDTO> findProvidersByLocation(String location) {
        return providerRepository.findByServiceAreaContaining(location).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProviderProfileDTO> findProviderById(Long id) {
        return providerRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProviderProfileDTO> findProviderByUserId(Long userId) {
        return providerRepository.findByUserId(userId).map(this::toDTO);
    }

    @Override
    @Transactional
    public ProviderProfileDTO createOrUpdateProfile(Long userId, ProviderProfileDTO profileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<ProviderProfile> existingProfile = providerRepository.findByUserId(userId);
        
        ProviderProfile profile;
        if (existingProfile.isPresent()) {
            // Update existing profile
            profile = existingProfile.get();
            updateProfileFields(profile, profileDTO);
        } else {
            // Create new profile
            profile = new ProviderProfile();
            profile.setUser(user);
            updateProfileFields(profile, profileDTO);
        }

        return toDTO(providerRepository.save(profile));
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        ProviderProfile profile = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found with id: " + id));
        providerRepository.delete(profile);
    }

    @Override
    @Transactional
    public ProviderProfileDTO updateAvailability(Long userId, boolean available) {
        ProviderProfile profile = providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found for user id: " + userId));
        
        profile.setAvailable(available);
        return toDTO(providerRepository.save(profile));
    }

    private void updateProfileFields(ProviderProfile profile, ProviderProfileDTO dto) {
        profile.setBio(dto.getBio());
        profile.setSkills(dto.getSkills());
        profile.setServiceArea(dto.getServiceArea());
        profile.setHourlyRate(dto.getHourlyRate());
        profile.setAvailable(dto.isAvailable());
        profile.setProfileImageUrl(dto.getProfileImageUrl());
        profile.setCertifications(dto.getCertifications());

        // Set service category if provided
        if (dto.getServiceCategoryId() != null) {
            ServiceCategory category = categoryRepository.findById(dto.getServiceCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + dto.getServiceCategoryId()));
            profile.setServiceCategory(category);
        }
    }

    private ProviderProfileDTO toDTO(ProviderProfile profile) {
        ProviderProfileDTO dto = modelMapper.map(profile, ProviderProfileDTO.class);
        
        // Map user details
        if (profile.getUser() != null) {
            dto.setUserId(profile.getUser().getId());
            dto.setUserName(profile.getUser().getName());
            dto.setUserEmail(profile.getUser().getEmail());
            dto.setUserLocation(profile.getUser().getLocation());
        }
        
        // Map service category details
        if (profile.getServiceCategory() != null) {
            dto.setServiceCategoryId(profile.getServiceCategory().getId());
            dto.setServiceCategoryName(profile.getServiceCategory().getName());
        }
        
        return dto;
    }
}
