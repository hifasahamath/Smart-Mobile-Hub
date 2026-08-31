package com.smartmobilehub.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateBrandRequest {
    @NotBlank(message = "Brand name is required")
    private String name;

    private String logoUrl;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
