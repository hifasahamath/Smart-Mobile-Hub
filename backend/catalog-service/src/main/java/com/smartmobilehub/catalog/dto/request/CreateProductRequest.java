package com.smartmobilehub.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class CreateProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Brand is required")
    private Long brandId;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Description is required")
    private String description;

    private String shortDescription;

    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be positive")
    private BigDecimal basePrice;

    private BigDecimal compareAtPrice;
    private String specifications;
    private boolean featured = false;
    private boolean trending = false;

    private List<ImageRequest> images;
    private List<VariantGroupRequest> variantGroups;
    private List<SkuRequest> skus;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public BigDecimal getCompareAtPrice() { return compareAtPrice; }
    public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }
    public String getSpecifications() { return specifications; }
    public void setSpecifications(String specifications) { this.specifications = specifications; }
    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public boolean isTrending() { return trending; }
    public void setTrending(boolean trending) { this.trending = trending; }
    public List<ImageRequest> getImages() { return images; }
    public void setImages(List<ImageRequest> images) { this.images = images; }
    public List<VariantGroupRequest> getVariantGroups() { return variantGroups; }
    public void setVariantGroups(List<VariantGroupRequest> variantGroups) { this.variantGroups = variantGroups; }
    public List<SkuRequest> getSkus() { return skus; }
    public void setSkus(List<SkuRequest> skus) { this.skus = skus; }

    // Nested request DTOs
    public static class ImageRequest {
        private String url;
        private String altText;
        private int sortOrder = 0;
        private boolean primary = false;

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }
        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
        public boolean isPrimary() { return primary; }
        public void setPrimary(boolean primary) { this.primary = primary; }
    }

    public static class VariantGroupRequest {
        private String name;
        private int sortOrder = 0;
        private List<String> options;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }

    public static class SkuRequest {
        private String skuCode;
        private BigDecimal price;
        private BigDecimal compareAtPrice;
        /** Map of variant group name → option value, e.g. {"Storage": "256GB", "Color": "Black"} */
        private java.util.Map<String, String> options;

        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getCompareAtPrice() { return compareAtPrice; }
        public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }
        public java.util.Map<String, String> getOptions() { return options; }
        public void setOptions(java.util.Map<String, String> options) { this.options = options; }
    }
}
