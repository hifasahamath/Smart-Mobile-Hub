package com.smartmobilehub.order.controller;

import com.smartmobilehub.order.dto.request.CheckoutRequest;
import com.smartmobilehub.order.dto.response.ApiResponse;
import com.smartmobilehub.order.dto.response.OrderResponse;
import com.smartmobilehub.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** Checkout — create a new order. Requires authentication. */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @Valid @RequestBody CheckoutRequest request,
            @RequestHeader("X-User-Email") String customerEmail) {
        OrderResponse order = orderService.checkout(request, customerEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created", order));
    }

    /** Get current customer's orders */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @RequestHeader("X-User-Email") String customerEmail,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderResponse> orders = orderService.getCustomerOrders(customerEmail, pageable);
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", orders));
    }

    /** Get order by ID */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved", order));
    }

    /** Cancel an order (customer) */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String customerEmail) {
        OrderResponse order = orderService.cancelOrder(id, customerEmail);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", order));
    }

    /** Update order status (admin) */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        OrderResponse order = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    /** Admin: list all orders */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved", orders));
    }
}
