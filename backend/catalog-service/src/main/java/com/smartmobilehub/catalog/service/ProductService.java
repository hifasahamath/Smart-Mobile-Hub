package com.smartmobilehub.catalog.service;

import com.smartmobilehub.catalog.dto.request.CreateProductRequest;
import com.smartmobilehub.catalog.dto.response.ProductResponse;
import com.smartmobilehub.catalog.entity.*;
import com.smartmobilehub.catalog.exception.BusinessException;
import com.smartmobilehub.catalog.exception.ResourceNotFoundException;
import com.smartmobilehub.catalog.mapper.CatalogMapper;
import com.smartmobilehub.catalog.repository.BrandRepository;
import com.smartmobilehub.catalog.repository.CategoryRepository;
import com.smartmobilehub.catalog.repository.ProductRepository;
import com.smartmobilehub.catalog.repository.SkuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SkuRepository skuRepository;
    private final CatalogMapper mapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          BrandRepository brandRepository, SkuRepository skuRepository, CatalogMapper mapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.skuRepository = skuRepository;
        this.mapper = mapper;
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(mapper::toProductSummaryResponse);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return mapper.toProductResponse(product);
    }

    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found", "PRODUCT_NOT_FOUND"));
        return mapper.toProductResponse(product);
    }

    public Page<ProductResponse> getFeaturedProducts(Pageable pageable) {
        return productRepository.findFeaturedProducts(pageable)
                .map(mapper::toProductSummaryResponse);
    }

    public Page<ProductResponse> getTrendingProducts(Pageable pageable) {
        return productRepository.findTrendingProducts(pageable)
                .map(mapper::toProductSummaryResponse);
    }

    public Page<ProductResponse> searchProducts(String search, Long categoryId, Long brandId,
                                                 BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(search, categoryId, brandId, minPrice, maxPrice, pageable)
                .map(mapper::toProductSummaryResponse);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", request.getBrandId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        String slug = generateSlug(request.getName());
        if (productRepository.existsBySlug(slug)) {
            // Append a unique suffix
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(slug);
        product.setBrand(brand);
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setBasePrice(request.getBasePrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setSpecifications(request.getSpecifications());
        product.setFeatured(request.isFeatured());
        product.setTrending(request.isTrending());

        // Add images
        if (request.getImages() != null) {
            for (CreateProductRequest.ImageRequest imgReq : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setUrl(imgReq.getUrl());
                image.setAltText(imgReq.getAltText());
                image.setSortOrder(imgReq.getSortOrder());
                image.setPrimary(imgReq.isPrimary());
                product.getImages().add(image);
            }
        }

        // Add variant groups and options
        Map<String, Map<String, VariantOption>> variantLookup = new HashMap<>();
        if (request.getVariantGroups() != null) {
            for (CreateProductRequest.VariantGroupRequest groupReq : request.getVariantGroups()) {
                VariantGroup group = new VariantGroup();
                group.setProduct(product);
                group.setName(groupReq.getName());
                group.setSortOrder(groupReq.getSortOrder());

                Map<String, VariantOption> optionMap = new HashMap<>();
                if (groupReq.getOptions() != null) {
                    int optionSort = 0;
                    for (String optionValue : groupReq.getOptions()) {
                        VariantOption option = new VariantOption();
                        option.setVariantGroup(group);
                        option.setValue(optionValue);
                        option.setSortOrder(optionSort++);
                        group.getOptions().add(option);
                        optionMap.put(optionValue, option);
                    }
                }
                variantLookup.put(groupReq.getName(), optionMap);
                product.getVariantGroups().add(group);
            }
        }

        // Save product first to get IDs for variant options
        Product savedProduct = productRepository.save(product);

        // Add SKUs (after variant options have IDs)
        if (request.getSkus() != null) {
            for (CreateProductRequest.SkuRequest skuReq : request.getSkus()) {
                if (skuRepository.existsBySkuCode(skuReq.getSkuCode())) {
                    throw new BusinessException("SKU code already exists: " + skuReq.getSkuCode(), "SKU_EXISTS");
                }

                Sku sku = new Sku();
                sku.setProduct(savedProduct);
                sku.setSkuCode(skuReq.getSkuCode());
                sku.setPrice(skuReq.getPrice());
                sku.setCompareAtPrice(skuReq.getCompareAtPrice());

                // Link SKU to its variant options
                if (skuReq.getOptions() != null) {
                    Set<VariantOption> options = new HashSet<>();
                    for (Map.Entry<String, String> entry : skuReq.getOptions().entrySet()) {
                        Map<String, VariantOption> optionMap = variantLookup.get(entry.getKey());
                        if (optionMap != null && optionMap.containsKey(entry.getValue())) {
                            options.add(optionMap.get(entry.getValue()));
                        }
                    }
                    sku.setOptionValues(options);
                }

                savedProduct.getSkus().add(sku);
            }
            savedProduct = productRepository.save(savedProduct);
        }

        return mapper.toProductResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand", request.getBrandId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));

        product.setName(request.getName());
        product.setBrand(brand);
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setBasePrice(request.getBasePrice());
        product.setCompareAtPrice(request.getCompareAtPrice());
        product.setSpecifications(request.getSpecifications());
        product.setFeatured(request.isFeatured());
        product.setTrending(request.isTrending());

        Product saved = productRepository.save(product);
        return mapper.toProductResponse(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setActive(false); // Soft delete
        productRepository.save(product);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
