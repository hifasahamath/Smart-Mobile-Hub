package com.smartmobilehub.inventory.controller;

import com.smartmobilehub.inventory.dto.request.AdjustStockRequest;
import com.smartmobilehub.inventory.dto.request.ReserveStockRequest;
import com.smartmobilehub.inventory.dto.response.ApiResponse;
import com.smartmobilehub.inventory.dto.response.InventoryResponse;
import com.smartmobilehub.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /** Check stock for a single SKU (public) */
    @GetMapping("/{skuCode}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getStock(@PathVariable String skuCode) {
        InventoryResponse stock = inventoryService.getStock(skuCode);
        return ResponseEntity.ok(ApiResponse.success("Stock retrieved", stock));
    }

    /** Check stock for multiple SKUs (used by order-service during checkout) */
    @GetMapping("/batch")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getStockBatch(
            @RequestParam List<String> skuCodes) {
        List<InventoryResponse> stocks = inventoryService.getStockBatch(skuCodes);
        return ResponseEntity.ok(ApiResponse.success("Stock batch retrieved", stocks));
    }

    /** Reserve stock during checkout (called by order-service) */
    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryResponse>> reserveStock(
            @Valid @RequestBody ReserveStockRequest request) {
        InventoryResponse result = inventoryService.reserveStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock reserved", result));
    }

    /** Release stock on cancellation (called by order-service or Kafka consumer) */
    @PostMapping("/release")
    public ResponseEntity<ApiResponse<InventoryResponse>> releaseStock(
            @Valid @RequestBody ReserveStockRequest request) {
        InventoryResponse result = inventoryService.releaseStock(request);
        return ResponseEntity.ok(ApiResponse.success("Stock released", result));
    }

    /** Admin: Set or adjust stock quantity */
    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<InventoryResponse>> adjustStock(
            @Valid @RequestBody AdjustStockRequest request,
            @RequestHeader(value = "X-User-Email", required = false) String adminEmail) {
        InventoryResponse result = inventoryService.adjustStock(request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Stock adjusted", result));
    }

    /** Admin: Get all low-stock items */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStock() {
        List<InventoryResponse> items = inventoryService.getLowStockItems();
        return ResponseEntity.ok(ApiResponse.success("Low stock items retrieved", items));
    }
}
