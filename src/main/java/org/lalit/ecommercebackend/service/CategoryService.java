package org.lalit.ecommercebackend.service;


import org.lalit.ecommercebackend.dto.CategoryDTO;
import org.lalit.ecommercebackend.exception.ResourceNotFoundException;
import org.lalit.ecommercebackend.model.Category;
import org.lalit.ecommercebackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new DataIntegrityViolationException("Category name already exists: " + categoryDTO.getName());
        }

        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryDTO.getId()));

        // Check if name is being changed and if it already exists
        if (!existingCategory.getName().equals(categoryDTO.getName()) &&
                categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new DataIntegrityViolationException("Category name already exists: " + categoryDTO.getName());
        }

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getProducts().isEmpty()) {
            throw new DataIntegrityViolationException("Cannot delete category with associated products");
        }

        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
