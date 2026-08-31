package com.smartmobilehub.order.exception;

public class CheckoutException extends RuntimeException {
    private final String code;

    public CheckoutException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
