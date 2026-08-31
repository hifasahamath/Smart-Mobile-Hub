package com.smartmobilehub.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CheckoutRequest {

    @NotEmpty(message = "Cart must have at least one item")
    private List<CartItem> items;

    @NotNull(message = "Delivery method is required")
    private String deliveryMethod; // HOME_DELIVERY or STORE_PICKUP

    /** Required for HOME_DELIVERY */
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryZoneName;

    @NotBlank(message = "Contact name is required")
    private String contactName;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // CASH_ON_DELIVERY, BANK_TRANSFER, PAY_AT_STORE

    private String notes;

    public static class CartItem {
        @NotBlank(message = "SKU code is required")
        private String skuCode;

        private String productName;
        private String variantDescription;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getVariantDescription() { return variantDescription; }
        public void setVariantDescription(String variantDescription) { this.variantDescription = variantDescription; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    // Getters and setters
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }
    public String getDeliveryZoneName() { return deliveryZoneName; }
    public void setDeliveryZoneName(String deliveryZoneName) { this.deliveryZoneName = deliveryZoneName; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
