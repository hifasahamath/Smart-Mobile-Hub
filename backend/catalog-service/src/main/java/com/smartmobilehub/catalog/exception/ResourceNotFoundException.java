package com.smartmobilehub.catalog.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String code;

    public ResourceNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
        this.code = resourceName.toUpperCase().replace(" ", "_") + "_NOT_FOUND";
    }

    public String getCode() { return code; }
}
