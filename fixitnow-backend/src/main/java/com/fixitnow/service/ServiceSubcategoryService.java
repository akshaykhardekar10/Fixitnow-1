package com.fixitnow.service;

import com.fixitnow.dto.ServiceSubcategoryDTO;

import java.util.List;
import java.util.Optional;

public interface ServiceSubcategoryService {
    
    List<ServiceSubcategoryDTO> findAllSubcategories();
    
    List<ServiceSubcategoryDTO> findActiveSubcategories();
    
    List<ServiceSubcategoryDTO> findSubcategoriesByCategory(Long categoryId);
    
    Optional<ServiceSubcategoryDTO> findSubcategoryById(Long id);
    
    ServiceSubcategoryDTO createSubcategory(ServiceSubcategoryDTO subcategoryDTO);
    
    ServiceSubcategoryDTO updateSubcategory(Long id, ServiceSubcategoryDTO subcategoryDTO);
    
    void deleteSubcategory(Long id);
}
