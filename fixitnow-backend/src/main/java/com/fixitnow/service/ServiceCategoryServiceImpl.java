package com.fixitnow.service;

import com.fixitnow.dto.ServiceCategoryDTO;
import com.fixitnow.exception.ResourceNotFoundException;
import com.fixitnow.model.ServiceCategory;
import com.fixitnow.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryDTO> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategoryDTO> findActiveCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceCategoryDTO> findCategoryById(Long id) {
        return categoryRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional
    public ServiceCategoryDTO createCategory(ServiceCategoryDTO categoryDTO) {
        if (existsByName(categoryDTO.getName())) {
            throw new IllegalStateException("Category with name '" + categoryDTO.getName() + "' already exists");
        }
        
        ServiceCategory category = toEntity(categoryDTO);
        category.setActive(true);
        
        return toDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public ServiceCategoryDTO updateCategory(Long id, ServiceCategoryDTO categoryDTO) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDTO.getName());
                    existingCategory.setDescription(categoryDTO.getDescription());
                    existingCategory.setIconUrl(categoryDTO.getIconUrl());
                    existingCategory.setActive(categoryDTO.isActive());
                    return toDTO(categoryRepository.save(existingCategory));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    private ServiceCategoryDTO toDTO(ServiceCategory category) {
        return modelMapper.map(category, ServiceCategoryDTO.class);
    }

    private ServiceCategory toEntity(ServiceCategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, ServiceCategory.class);
    }
}
