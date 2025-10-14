package com.fixitnow.service;

import com.fixitnow.dto.ServiceListingDTO;
import com.fixitnow.dto.ServiceSearchRequest;
import com.fixitnow.exception.ResourceNotFoundException;
import com.fixitnow.model.*;
import com.fixitnow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceListingServiceImpl implements ServiceListingService {

    private final ServiceListingRepository serviceListingRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ServiceSubcategoryRepository subcategoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceListingDTO> findServicesByProvider(Long providerProfileId) {
        return serviceListingRepository.findByProviderProfileIdAndActiveTrue(providerProfileId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceListingDTO> findAllActiveServices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return serviceListingRepository.findByActiveTrue(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceListingDTO> findServicesByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return serviceListingRepository.findByCategoryIdAndActiveTrue(categoryId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceListingDTO> findServicesBySubcategory(Long subcategoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return serviceListingRepository.findBySubcategoryIdAndActiveTrue(subcategoryId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceListingDTO> searchServices(ServiceSearchRequest searchRequest) {
        Sort sort = createSort(searchRequest.getSortBy(), searchRequest.getSortOrder());
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().isEmpty()) {
            return serviceListingRepository.searchByKeyword(searchRequest.getKeyword(), pageable).map(this::toDTO);
        }
        
        return serviceListingRepository.searchServices(
                searchRequest.getCategoryId(),
                searchRequest.getSubcategoryId(),
                searchRequest.getLocation(),
                searchRequest.getMinPrice(),
                searchRequest.getMaxPrice(),
                pageable
        ).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceListingDTO> findServiceById(Long id) {
        return serviceListingRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional
    public ServiceListingDTO createService(ServiceListingDTO serviceDTO, Long userId) {
        ProviderProfile providerProfile = providerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found for user"));
        
        ServiceCategory category = categoryRepository.findById(serviceDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        ServiceSubcategory subcategory = null;
        if (serviceDTO.getSubcategoryId() != null) {
            subcategory = subcategoryRepository.findById(serviceDTO.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
        }
        
        ServiceListing serviceListing = ServiceListing.builder()
                .providerProfile(providerProfile)
                .category(category)
                .subcategory(subcategory)
                .title(serviceDTO.getTitle())
                .description(serviceDTO.getDescription())
                .price(serviceDTO.getPrice())
                .pricingType(serviceDTO.getPricingType())
                .serviceLocation(serviceDTO.getServiceLocation())
                .imageUrls(serviceDTO.getImageUrls())
                .estimatedDuration(serviceDTO.getEstimatedDuration())
                .availabilityDays(serviceDTO.getAvailabilityDays())
                .active(true)
                .viewCount(0)
                .bookingCount(0)
                .build();
        
        return toDTO(serviceListingRepository.save(serviceListing));
    }

    @Override
    @Transactional
    public ServiceListingDTO updateService(Long id, ServiceListingDTO serviceDTO, Long userId) {
        ServiceListing serviceListing = serviceListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service listing not found"));
        
        // Verify ownership
        if (!serviceListing.getProviderProfile().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to update this service");
        }
        
        serviceListing.setTitle(serviceDTO.getTitle());
        serviceListing.setDescription(serviceDTO.getDescription());
        serviceListing.setPrice(serviceDTO.getPrice());
        serviceListing.setPricingType(serviceDTO.getPricingType());
        serviceListing.setServiceLocation(serviceDTO.getServiceLocation());
        serviceListing.setImageUrls(serviceDTO.getImageUrls());
        serviceListing.setEstimatedDuration(serviceDTO.getEstimatedDuration());
        serviceListing.setAvailabilityDays(serviceDTO.getAvailabilityDays());
        serviceListing.setActive(serviceDTO.isActive());
        
        if (serviceDTO.getCategoryId() != null) {
            ServiceCategory category = categoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            serviceListing.setCategory(category);
        }
        
        if (serviceDTO.getSubcategoryId() != null) {
            ServiceSubcategory subcategory = subcategoryRepository.findById(serviceDTO.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
            serviceListing.setSubcategory(subcategory);
        }
        
        return toDTO(serviceListingRepository.save(serviceListing));
    }

    @Override
    @Transactional
    public void deleteService(Long id, Long userId) {
        ServiceListing serviceListing = serviceListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service listing not found"));
        
        // Verify ownership
        if (!serviceListing.getProviderProfile().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to delete this service");
        }
        
        serviceListingRepository.delete(serviceListing);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        ServiceListing serviceListing = serviceListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service listing not found"));
        serviceListing.setViewCount(serviceListing.getViewCount() + 1);
        serviceListingRepository.save(serviceListing);
    }

    @Override
    @Transactional
    public void incrementBookingCount(Long id) {
        ServiceListing serviceListing = serviceListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service listing not found"));
        serviceListing.setBookingCount(serviceListing.getBookingCount() + 1);
        serviceListingRepository.save(serviceListing);
    }

    private ServiceListingDTO toDTO(ServiceListing service) {
        ServiceListingDTO dto = ServiceListingDTO.builder()
                .id(service.getId())
                .providerProfileId(service.getProviderProfile().getId())
                .providerName(service.getProviderProfile().getUser().getName())
                .providerRating(service.getProviderProfile().getRating())
                .providerTotalReviews(service.getProviderProfile().getTotalReviews())
                .providerImageUrl(service.getProviderProfile().getProfileImageUrl())
                .categoryId(service.getCategory().getId())
                .categoryName(service.getCategory().getName())
                .subcategoryId(service.getSubcategory() != null ? service.getSubcategory().getId() : null)
                .subcategoryName(service.getSubcategory() != null ? service.getSubcategory().getName() : null)
                .title(service.getTitle())
                .description(service.getDescription())
                .price(service.getPrice())
                .pricingType(service.getPricingType())
                .serviceLocation(service.getServiceLocation())
                .imageUrls(service.getImageUrls())
                .estimatedDuration(service.getEstimatedDuration())
                .availabilityDays(service.getAvailabilityDays())
                .active(service.isActive())
                .viewCount(service.getViewCount())
                .bookingCount(service.getBookingCount())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
        return dto;
    }

    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        
        switch (sortBy.toLowerCase()) {
            case "price":
                return Sort.by(direction, "price");
            case "rating":
                return Sort.by(Sort.Direction.DESC, "providerProfile.rating");
            case "popular":
                return Sort.by(Sort.Direction.DESC, "bookingCount");
            case "recent":
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}
