package com.smartmobilehub.catalog.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "variant_options")
public class VariantOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_group_id", nullable = false)
    private VariantGroup variantGroup;

    /** e.g. "128GB", "Black", "8GB" */
    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private int sortOrder = 0;

    @ManyToMany(mappedBy = "optionValues")
    private Set<Sku> skus = new HashSet<>();

    public VariantOption() {}

    // Getters and setters
    public Long getId() { return id; }
    public VariantGroup getVariantGroup() { return variantGroup; }
    public void setVariantGroup(VariantGroup variantGroup) { this.variantGroup = variantGroup; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public Set<Sku> getSkus() { return skus; }
}
