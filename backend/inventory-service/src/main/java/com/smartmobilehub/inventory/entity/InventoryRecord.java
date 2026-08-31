package com.smartmobilehub.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks the available and reserved quantity for each SKU.
 * Uses pessimistic locking to prevent overselling during concurrent purchases.
 */
@Entity
@Table(name = "inventory_records", uniqueConstraints = @UniqueConstraint(columnNames = "skuCode"))
public class InventoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** References a SKU code from catalog-service — NOT a foreign key (cross-service boundary) */
    @Column(unique = true, nullable = false)
    private String skuCode;

    @Column(nullable = false)
    private int quantity = 0;

    @Column(nullable = false)
    private int reservedQuantity = 0;

    @Column(nullable = false)
    private int lowStockThreshold = 5;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Available quantity = total - reserved */
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public boolean isLowStock() {
        return getAvailableQuantity() <= lowStockThreshold;
    }

    public InventoryRecord() {}

    // Getters and setters
    public Long getId() { return id; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
