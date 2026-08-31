package com.smartmobilehub.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
    public OrderNotFoundException(String orderNumber) {
        super("Order not found: " + orderNumber);
    }
}
