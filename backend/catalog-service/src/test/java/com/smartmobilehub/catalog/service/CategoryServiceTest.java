package com.smartmobilehub.catalog.service;

import com.smartmobilehub.catalog.dto.request.CreateCategoryRequest;
import com.smartmobilehub.catalog.dto.response.CategoryResponse;
import com.smartmobilehub.catalog.entity.Category;
import com.smartmobilehub.catalog.exception.BusinessException;
import com.smartmobilehub.catalog.exception.ResourceNotFoundException;
import com.smartmobilehub.catalog.mapper.CatalogMapper;
import com.smartmobilehub.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CatalogMapper mapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryResponse testResponse;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setName("Smartphones");
        testCategory.setSlug("smartphones");
        testCategory.setDescription("Latest smartphones");

        testResponse = new CategoryResponse();
        testResponse.setId(1L);
        testResponse.setName("Smartphones");
        testResponse.setSlug("smartphones");
    }

    @Test
    void getAllCategories_returnsList() {
        when(categoryRepository.findByActiveTrueOrderBySortOrderAsc())
                .thenReturn(List.of(testCategory));
        when(mapper.toCategoryResponse(testCategory)).thenReturn(testResponse);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertEquals(1, result.size());
        assertEquals("Smartphones", result.get(0).getName());
    }

    @Test
    void getCategoryById_found() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(mapper.toCategoryResponse(testCategory)).thenReturn(testResponse);

        CategoryResponse result = categoryService.getCategoryById(1L);

        assertEquals("Smartphones", result.getName());
    }

    @Test
    void getCategoryById_notFound_throwsException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(999L));
    }

    @Test
    void createCategory_success() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Tablets");
        request.setDescription("All tablets");

        when(categoryRepository.existsBySlug("tablets")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(mapper.toCategoryResponse(any())).thenReturn(testResponse);

        CategoryResponse result = categoryService.createCategory(request);

        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_duplicateSlug_throwsException() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Smartphones");

        when(categoryRepository.existsBySlug("smartphones")).thenReturn(true);

        assertThrows(BusinessException.class, () -> categoryService.createCategory(request));
    }

    @Test
    void deleteCategory_softDeletes() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any())).thenReturn(testCategory);

        categoryService.deleteCategory(1L);

        assertFalse(testCategory.isActive());
        verify(categoryRepository).save(testCategory);
    }
}
