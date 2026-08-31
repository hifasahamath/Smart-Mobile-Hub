package com.smartmobilehub.order.entity;

/**
 * Order state machine:
 * PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
 *                 ↘ CANCELLED (from PENDING or CONFIRMED only)
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    /**
     * Validates that a status transition is legal.
     * Returns true if transitioning from this status to the target is allowed.
     */
    public boolean canTransitionTo(OrderStatus target) {
        return switch (this) {
            case PENDING -> target == CONFIRMED || target == CANCELLED;
            case CONFIRMED -> target == PROCESSING || target == CANCELLED;
            case PROCESSING -> target == SHIPPED;
            case SHIPPED -> target == DELIVERED;
            case DELIVERED, CANCELLED -> false; // terminal states
        };
    }
}
