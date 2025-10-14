package com.fixitnow.service;

import com.fixitnow.dto.ServiceCategoryDTO;

import java.util.List;
import java.util.Optional;

public interface ServiceCategoryService {
    List<ServiceCategoryDTO> findAllCategories();
    List<ServiceCategoryDTO> findActiveCategories();
    Optional<ServiceCategoryDTO> findCategoryById(Long id);
    ServiceCategoryDTO createCategory(ServiceCategoryDTO categoryDTO);
    ServiceCategoryDTO updateCategory(Long id, ServiceCategoryDTO categoryDTO);
    void deleteCategory(Long id);
    boolean existsByName(String name);
}
