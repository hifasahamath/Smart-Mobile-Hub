package com.smartmobilehub.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit trail for all inventory changes.
 * Every stock adjustment (reserve, release, manual adjust) is logged here.
 */
@Entity
@Table(name = "inventory_history")
public class InventoryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String skuCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(nullable = false)
    private int quantityChange;

    private int quantityBefore;
    private int quantityAfter;

    /** Optional reference (e.g. orderId that triggered the reservation) */
    private String referenceId;

    private String performedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ActionType {
        RESERVE,      // Stock reserved during checkout
        RELEASE,      // Stock released on cancellation
        ADJUST,       // Manual admin adjustment
        INITIAL_SET   // Initial stock setting
    }

    public InventoryHistory() {}

    // Getters and setters
    public Long getId() { return id; }
    public String getSkuCode() { return skuCode; }
    public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
    public ActionType getActionType() { return actionType; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
    public int getQuantityChange() { return quantityChange; }
    public void setQuantityChange(int quantityChange) { this.quantityChange = quantityChange; }
    public int getQuantityBefore() { return quantityBefore; }
    public void setQuantityBefore(int quantityBefore) { this.quantityBefore = quantityBefore; }
    public int getQuantityAfter() { return quantityAfter; }
    public void setQuantityAfter(int quantityAfter) { this.quantityAfter = quantityAfter; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
