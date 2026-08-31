package com.smartmobilehub.catalog.service;

import com.smartmobilehub.catalog.dto.response.ProductResponse;
import com.smartmobilehub.catalog.entity.Brand;
import com.smartmobilehub.catalog.entity.Category;
import com.smartmobilehub.catalog.entity.Product;
import com.smartmobilehub.catalog.exception.ResourceNotFoundException;
import com.smartmobilehub.catalog.mapper.CatalogMapper;
import com.smartmobilehub.catalog.repository.BrandRepository;
import com.smartmobilehub.catalog.repository.CategoryRepository;
import com.smartmobilehub.catalog.repository.ProductRepository;
import com.smartmobilehub.catalog.repository.SkuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private SkuRepository skuRepository;
    @Mock private CatalogMapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductResponse testResponse;
    private Brand testBrand;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testBrand = new Brand();
        testBrand.setName("Apple");
        testBrand.setSlug("apple");

        testCategory = new Category();
        testCategory.setName("Smartphones");
        testCategory.setSlug("smartphones");

        testProduct = new Product();
        testProduct.setName("iPhone 15 Pro");
        testProduct.setSlug("iphone-15-pro");
        testProduct.setBrand(testBrand);
        testProduct.setCategory(testCategory);
        testProduct.setBasePrice(new BigDecimal("999.00"));
        testProduct.setDescription("Latest iPhone");

        testResponse = new ProductResponse();
        testResponse.setId(1L);
        testResponse.setName("iPhone 15 Pro");
        testResponse.setSlug("iphone-15-pro");
        testResponse.setBasePrice(new BigDecimal("999.00"));
    }

    @Test
    void getAllProducts_returnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productRepository.findByActiveTrue(pageable)).thenReturn(productPage);
        when(mapper.toProductSummaryResponse(testProduct)).thenReturn(testResponse);

        Page<ProductResponse> result = productService.getAllProducts(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("iPhone 15 Pro", result.getContent().get(0).getName());
    }

    @Test
    void getProductById_found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(mapper.toProductResponse(testProduct)).thenReturn(testResponse);

        ProductResponse result = productService.getProductById(1L);

        assertEquals("iPhone 15 Pro", result.getName());
    }

    @Test
    void getProductById_notFound_throwsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void getProductBySlug_found() {
        when(productRepository.findBySlug("iphone-15-pro")).thenReturn(Optional.of(testProduct));
        when(mapper.toProductResponse(testProduct)).thenReturn(testResponse);

        ProductResponse result = productService.getProductBySlug("iphone-15-pro");

        assertEquals("iPhone 15 Pro", result.getName());
    }

    @Test
    void searchProducts_delegatesToRepository() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = new PageImpl<>(List.of(testProduct));

        when(productRepository.searchProducts("iPhone", null, null, null, null, pageable))
                .thenReturn(productPage);
        when(mapper.toProductSummaryResponse(testProduct)).thenReturn(testResponse);

        Page<ProductResponse> result = productService.searchProducts(
                "iPhone", null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void deleteProduct_softDeletes() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any())).thenReturn(testProduct);

        productService.deleteProduct(1L);

        assertFalse(testProduct.isActive());
        verify(productRepository).save(testProduct);
    }
}
