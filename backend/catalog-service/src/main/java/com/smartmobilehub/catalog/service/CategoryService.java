package com.smartmobilehub.catalog.service;

import com.smartmobilehub.catalog.dto.request.CreateCategoryRequest;
import com.smartmobilehub.catalog.dto.response.CategoryResponse;
import com.smartmobilehub.catalog.entity.Category;
import com.smartmobilehub.catalog.exception.BusinessException;
import com.smartmobilehub.catalog.exception.ResourceNotFoundException;
import com.smartmobilehub.catalog.mapper.CatalogMapper;
import com.smartmobilehub.catalog.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CatalogMapper mapper;

    public CategoryService(CategoryRepository categoryRepository, CatalogMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(mapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(mapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return mapper.toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        String slug = generateSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("Category with this name already exists", "CATEGORY_EXISTS");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.getParentId()));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return mapper.toCategoryResponse(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));

        String newSlug = generateSlug(request.getName());
        if (!newSlug.equals(category.getSlug()) && categoryRepository.existsBySlug(newSlug)) {
            throw new BusinessException("Category with this name already exists", "CATEGORY_EXISTS");
        }

        category.setName(request.getName());
        category.setSlug(newSlug);
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("Category cannot be its own parent", "INVALID_PARENT");
            }
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category", request.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category saved = categoryRepository.save(category);
        return mapper.toCategoryResponse(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        category.setActive(false); // Soft delete
        categoryRepository.save(category);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
