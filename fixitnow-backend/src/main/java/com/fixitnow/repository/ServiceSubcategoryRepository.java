package com.fixitnow.repository;

import com.fixitnow.model.ServiceSubcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceSubcategoryRepository extends JpaRepository<ServiceSubcategory, Long> {
    
    List<ServiceSubcategory> findByCategoryIdAndActiveTrue(Long categoryId);
    
    List<ServiceSubcategory> findByCategoryId(Long categoryId);
    
    List<ServiceSubcategory> findByActiveTrue();
    
    boolean existsByName(String name);
}
