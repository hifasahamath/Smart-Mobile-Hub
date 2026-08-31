package com.smartmobilehub.gateway.config;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * Determines which routes are publicly accessible (no JWT required)
 * and which require authentication.
 */
@Component
public class RouteValidator {

    /**
     * Endpoints that can be accessed without a valid JWT token.
     * All other endpoints require authentication.
     */
    private static final List<String> OPEN_ENDPOINTS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/products",
            "/api/v1/products/",
            "/api/v1/products/featured",
            "/api/v1/products/trending",
            "/api/v1/products/search",
            "/api/v1/products/slug/",
            "/api/v1/categories",
            "/api/v1/brands",
            "/api/v1/delivery-zones",
            "/actuator"
    );

    /**
     * Returns true if the request targets a secured (non-public) endpoint.
     */
    public Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_ENDPOINTS.stream()
                    .noneMatch(uri -> request.getURI().getPath().startsWith(uri));

    /**
     * Endpoints that specifically require ADMIN role.
     * These are checked after JWT validation passes.
     */
    private static final List<String> ADMIN_ENDPOINTS = List.of(
            "/api/v1/analytics",
            "/api/v1/inventory/adjust",
            "/api/v1/inventory/low-stock"
    );

    /**
     * Returns true if the request targets an admin-only endpoint.
     */
    public boolean isAdminEndpoint(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String method = request.getMethod().name();

        // POST/PUT/DELETE on products, categories, brands, delivery-zones are admin-only
        if (("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method))
                && (path.startsWith("/api/v1/products")
                || path.startsWith("/api/v1/categories")
                || path.startsWith("/api/v1/brands")
                || path.startsWith("/api/v1/delivery-zones"))) {
            return true;
        }

        // Payment verify/reject are admin-only
        if ("POST".equals(method) && (path.contains("/verify") || path.contains("/reject"))
                && path.startsWith("/api/v1/payments")) {
            return true;
        }

        return ADMIN_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
}
