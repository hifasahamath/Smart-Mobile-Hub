package com.smartmobilehub.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    private final String skuCode;
    private final int requested;
    private final int available;

    public InsufficientStockException(String skuCode, int requested, int available) {
        super(String.format("Insufficient stock for SKU %s: requested %d, available %d",
                skuCode, requested, available));
        this.skuCode = skuCode;
        this.requested = requested;
        this.available = available;
    }

    public String getSkuCode() { return skuCode; }
    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}
