package com.smartmobilehub.order.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void pending_canTransitionToConfirmed() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    void pending_canTransitionToCancelled() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void pending_cannotTransitionToShipped() {
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.SHIPPED));
    }

    @Test
    void confirmed_canTransitionToProcessing() {
        assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.PROCESSING));
    }

    @Test
    void confirmed_canTransitionToCancelled() {
        assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void processing_canTransitionToShipped() {
        assertTrue(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.SHIPPED));
    }

    @Test
    void processing_cannotTransitionToCancelled() {
        assertFalse(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void shipped_canTransitionToDelivered() {
        assertTrue(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED));
    }

    @Test
    void delivered_isTerminal() {
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.PENDING));
    }

    @Test
    void cancelled_isTerminal() {
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.CONFIRMED));
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.PENDING));
    }
}
