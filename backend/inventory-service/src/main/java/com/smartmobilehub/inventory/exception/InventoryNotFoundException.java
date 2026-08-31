package com.smartmobilehub.inventory.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String skuCode) {
        super("Inventory record not found for SKU: " + skuCode);
    }
}
