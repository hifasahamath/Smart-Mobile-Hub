package com.smartmobilehub.catalog.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A SKU (Stock Keeping Unit) represents an actual purchasable variant.
 * Example: IPH15P-256-BLACK = iPhone 15 Pro, 256GB, Black
 */
@Entity
@Table(name = "skus")
public class Sku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Unique SKU code, e.g. "IPH15P-256-BLACK" */
    @Column(unique = true, nullable = false)
    private String skuCode;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal compareAtPrice;

    @Column(nullable = false)
    private boolean active = true;

    /**
     * Many-to-many relationship with VariantOption.
     * A SKU is defined by its combination of variant options
     * (e.g. Storage=256GB + Color=Black).
     */
    @ManyToMany
    @JoinTable(
            name = "sku_option_values",
            joinColumns = @JoinColumn(name = "sku_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<VariantOption> optionValues = new HashSet<>();

    public Sku() {}

    // Getters and setters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getCompareAtPrice() { return compareAtPrice; }
    public void setCompareAtPrice(BigDecimal compareAtPrice) { this.compareAtPrice = compareAtPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Set<VariantOption> getOptionValues() { return optionValues; }
    public void setOptionValues(Set<VariantOption> optionValues) { this.optionValues = optionValues; }
}
