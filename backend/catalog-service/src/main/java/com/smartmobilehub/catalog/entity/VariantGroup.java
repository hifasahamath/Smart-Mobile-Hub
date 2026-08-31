package com.smartmobilehub.catalog.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "variant_groups")
public class VariantGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** e.g. "Storage", "Color", "RAM" */
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int sortOrder = 0;

    @OneToMany(mappedBy = "variantGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<VariantOption> options = new ArrayList<>();

    public VariantGroup() {}

    // Getters and setters
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public List<VariantOption> getOptions() { return options; }
}
