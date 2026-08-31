package com.smartmobilehub.catalog.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {
    private Long id;
    private String name;
    private String slug;
    private String brandName;
    private Long brandId;
    private String categoryName;
    private Long categoryId;
    private String description;
    private String shortDescription;
    private BigDecimal basePrice;
    private BigDecimal compareAtPrice;
    private String specifications;
    private boolean featured;
    private boolean trending;
    private boolean active;
    private List<ImageResponse> images;
    private List<VariantGroupResponse> variantGroups;
    private List<SkuResponse> skus;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
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
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<ImageResponse> getImages() { return images; }
    public void setImages(List<ImageResponse> images) { this.images = images; }
    public List<VariantGroupResponse> getVariantGroups() { return variantGroups; }
    public void setVariantGroups(List<VariantGroupResponse> variantGroups) { this.variantGroups = variantGroups; }
    public List<SkuResponse> getSkus() { return skus; }
    public void setSkus(List<SkuResponse> skus) { this.skus = skus; }

    // Nested response DTOs
    public static class ImageResponse {
        private Long id;
        private String url;
        private String altText;
        private int sortOrder;
        private boolean primary;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }
        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
        public boolean isPrimary() { return primary; }
        public void setPrimary(boolean primary) { this.primary = primary; }
    }

    public static class VariantGroupResponse {
        private Long id;
        private String name;
        private int sortOrder;
        private List<VariantOptionResponse> options;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
        public List<VariantOptionResponse> getOptions() { return options; }
        public void setOptions(List<VariantOptionResponse> options) { this.options = options; }
    }

    public static class VariantOptionResponse {
        private Long id;
        private String value;
        private int sortOrder;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public int getSortOrder() { return sortOrder; }
        public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    }

    public static class SkuResponse {
        private Long id;
        private String skuCode;
        private BigDecimal price;
        private BigDecimal compareAtPrice;
        private boolean active;
        private List<SkuOptionResponse> options;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getCompareAtPrice() { return compareAtPrice; }
        public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public List<SkuOptionResponse> getOptions() { return options; }
        public void setOptions(List<SkuOptionResponse> options) { this.options = options; }
    }

    public static class SkuOptionResponse {
        private String groupName;
        private String value;

        public String getGroupName() { return groupName; }
        public void setGroupName(String groupName) { this.groupName = groupName; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
