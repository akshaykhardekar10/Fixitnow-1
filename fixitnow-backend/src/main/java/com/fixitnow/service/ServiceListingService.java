package com.fixitnow.service;

import com.fixitnow.dto.ServiceListingDTO;
import com.fixitnow.dto.ServiceSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ServiceListingService {
    
    List<ServiceListingDTO> findServicesByProvider(Long providerProfileId);
    
    Page<ServiceListingDTO> findAllActiveServices(int page, int size);
    
    Page<ServiceListingDTO> findServicesByCategory(Long categoryId, int page, int size);
    
    Page<ServiceListingDTO> findServicesBySubcategory(Long subcategoryId, int page, int size);
    
    Page<ServiceListingDTO> searchServices(ServiceSearchRequest searchRequest);
    
    Optional<ServiceListingDTO> findServiceById(Long id);
    
    ServiceListingDTO createService(ServiceListingDTO serviceDTO, Long userId);
    
    ServiceListingDTO updateService(Long id, ServiceListingDTO serviceDTO, Long userId);
    
    void deleteService(Long id, Long userId);
    
    void incrementViewCount(Long id);
    
    void incrementBookingCount(Long id);
}
