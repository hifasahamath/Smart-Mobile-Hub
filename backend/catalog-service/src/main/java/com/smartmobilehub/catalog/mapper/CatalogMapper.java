package com.smartmobilehub.catalog.mapper;

import com.smartmobilehub.catalog.dto.response.BrandResponse;
import com.smartmobilehub.catalog.dto.response.CategoryResponse;
import com.smartmobilehub.catalog.dto.response.ProductResponse;
import com.smartmobilehub.catalog.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps JPA entities to response DTOs.
 * Keeps the mapping logic out of services and controllers.
 */
@Component
public class CatalogMapper {

    public CategoryResponse toCategoryResponse(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setActive(category.isActive());
        dto.setSortOrder(category.getSortOrder());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }
        return dto;
    }

    public BrandResponse toBrandResponse(Brand brand) {
        BrandResponse dto = new BrandResponse();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getSlug());
        dto.setLogoUrl(brand.getLogoUrl());
        dto.setDescription(brand.getDescription());
        dto.setActive(brand.isActive());
        return dto;
    }

    public ProductResponse toProductResponse(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setBrandName(product.getBrand().getName());
        dto.setBrandId(product.getBrand().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCategoryId(product.getCategory().getId());
        dto.setDescription(product.getDescription());
        dto.setShortDescription(product.getShortDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setCompareAtPrice(product.getCompareAtPrice());
        dto.setSpecifications(product.getSpecifications());
        dto.setFeatured(product.isFeatured());
        dto.setTrending(product.isTrending());
        dto.setActive(product.isActive());

        // Map images
        dto.setImages(product.getImages().stream()
                .map(this::toImageResponse)
                .collect(Collectors.toList()));

        // Map variant groups
        dto.setVariantGroups(product.getVariantGroups().stream()
                .map(this::toVariantGroupResponse)
                .collect(Collectors.toList()));

        // Map SKUs
        dto.setSkus(product.getSkus().stream()
                .map(this::toSkuResponse)
                .collect(Collectors.toList()));

        return dto;
    }

    /** Lightweight product response without variants/SKUs — used for list views */
    public ProductResponse toProductSummaryResponse(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setBrandName(product.getBrand().getName());
        dto.setBrandId(product.getBrand().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setCategoryId(product.getCategory().getId());
        dto.setShortDescription(product.getShortDescription());
        dto.setBasePrice(product.getBasePrice());
        dto.setCompareAtPrice(product.getCompareAtPrice());
        dto.setFeatured(product.isFeatured());
        dto.setTrending(product.isTrending());
        dto.setActive(product.isActive());

        // Only include primary image for list views
        dto.setImages(product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(this::toImageResponse)
                .collect(Collectors.toList()));

        return dto;
    }

    private ProductResponse.ImageResponse toImageResponse(ProductImage image) {
        ProductResponse.ImageResponse dto = new ProductResponse.ImageResponse();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        dto.setAltText(image.getAltText());
        dto.setSortOrder(image.getSortOrder());
        dto.setPrimary(image.isPrimary());
        return dto;
    }

    private ProductResponse.VariantGroupResponse toVariantGroupResponse(VariantGroup group) {
        ProductResponse.VariantGroupResponse dto = new ProductResponse.VariantGroupResponse();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setSortOrder(group.getSortOrder());
        dto.setOptions(group.getOptions().stream()
                .map(this::toVariantOptionResponse)
                .collect(Collectors.toList()));
        return dto;
    }

    private ProductResponse.VariantOptionResponse toVariantOptionResponse(VariantOption option) {
        ProductResponse.VariantOptionResponse dto = new ProductResponse.VariantOptionResponse();
        dto.setId(option.getId());
        dto.setValue(option.getValue());
        dto.setSortOrder(option.getSortOrder());
        return dto;
    }

    private ProductResponse.SkuResponse toSkuResponse(Sku sku) {
        ProductResponse.SkuResponse dto = new ProductResponse.SkuResponse();
        dto.setId(sku.getId());
        dto.setSkuCode(sku.getSkuCode());
        dto.setPrice(sku.getPrice());
        dto.setCompareAtPrice(sku.getCompareAtPrice());
        dto.setActive(sku.isActive());
        dto.setOptions(sku.getOptionValues().stream()
                .map(opt -> {
                    ProductResponse.SkuOptionResponse optDto = new ProductResponse.SkuOptionResponse();
                    optDto.setGroupName(opt.getVariantGroup().getName());
                    optDto.setValue(opt.getValue());
                    return optDto;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
