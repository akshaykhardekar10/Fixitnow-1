package com.fixitnow.service;

import com.fixitnow.dto.ServiceSubcategoryDTO;
import com.fixitnow.exception.ResourceNotFoundException;
import com.fixitnow.model.ServiceCategory;
import com.fixitnow.model.ServiceSubcategory;
import com.fixitnow.repository.ServiceCategoryRepository;
import com.fixitnow.repository.ServiceSubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceSubcategoryServiceImpl implements ServiceSubcategoryService {

    private final ServiceSubcategoryRepository subcategoryRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceSubcategoryDTO> findAllSubcategories() {
        return subcategoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceSubcategoryDTO> findActiveSubcategories() {
        return subcategoryRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceSubcategoryDTO> findSubcategoriesByCategory(Long categoryId) {
        return subcategoryRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceSubcategoryDTO> findSubcategoryById(Long id) {
        return subcategoryRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional
    public ServiceSubcategoryDTO createSubcategory(ServiceSubcategoryDTO subcategoryDTO) {
        ServiceCategory category = categoryRepository.findById(subcategoryDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + subcategoryDTO.getCategoryId()));
        
        ServiceSubcategory subcategory = ServiceSubcategory.builder()
                .name(subcategoryDTO.getName())
                .description(subcategoryDTO.getDescription())
                .category(category)
                .active(true)
                .build();
        
        return toDTO(subcategoryRepository.save(subcategory));
    }

    @Override
    @Transactional
    public ServiceSubcategoryDTO updateSubcategory(Long id, ServiceSubcategoryDTO subcategoryDTO) {
        ServiceSubcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + id));
        
        subcategory.setName(subcategoryDTO.getName());
        subcategory.setDescription(subcategoryDTO.getDescription());
        subcategory.setActive(subcategoryDTO.isActive());
        
        if (subcategoryDTO.getCategoryId() != null) {
            ServiceCategory category = categoryRepository.findById(subcategoryDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + subcategoryDTO.getCategoryId()));
            subcategory.setCategory(category);
        }
        
        return toDTO(subcategoryRepository.save(subcategory));
    }

    @Override
    @Transactional
    public void deleteSubcategory(Long id) {
        ServiceSubcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory not found with id: " + id));
        subcategoryRepository.delete(subcategory);
    }

    private ServiceSubcategoryDTO toDTO(ServiceSubcategory subcategory) {
        ServiceSubcategoryDTO dto = modelMapper.map(subcategory, ServiceSubcategoryDTO.class);
        dto.setCategoryId(subcategory.getCategory().getId());
        dto.setCategoryName(subcategory.getCategory().getName());
        return dto;
    }
}
