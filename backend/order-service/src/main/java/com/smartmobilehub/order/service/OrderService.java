package com.smartmobilehub.order.service;

import com.smartmobilehub.order.dto.request.CheckoutRequest;
import com.smartmobilehub.order.dto.response.OrderResponse;
import com.smartmobilehub.order.entity.*;
import com.smartmobilehub.order.exception.*;
import com.smartmobilehub.order.repository.DeliveryZoneRepository;
import com.smartmobilehub.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;
    private final RestTemplate restTemplate;

    @Value("${services.inventory-url}")
    private String inventoryServiceUrl;

    public OrderService(OrderRepository orderRepository, DeliveryZoneRepository deliveryZoneRepository,
                        RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.deliveryZoneRepository = deliveryZoneRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Full checkout flow:
     * 1. Validate delivery method + zone
     * 2. Reserve stock for each item via inventory-service
     * 3. Calculate backend-authoritative totals
     * 4. Create order in PENDING state
     */
    @Transactional
    public OrderResponse checkout(CheckoutRequest request, String customerEmail) {
        Order.DeliveryMethod deliveryMethod;
        try {
            deliveryMethod = Order.DeliveryMethod.valueOf(request.getDeliveryMethod());
        } catch (IllegalArgumentException e) {
            throw new CheckoutException("Invalid delivery method", "INVALID_DELIVERY_METHOD");
        }

        Order.PaymentMethod paymentMethod;
        try {
            paymentMethod = Order.PaymentMethod.valueOf(request.getPaymentMethod());
        } catch (IllegalArgumentException e) {
            throw new CheckoutException("Invalid payment method", "INVALID_PAYMENT_METHOD");
        }

        // Validate delivery zone for HOME_DELIVERY
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (deliveryMethod == Order.DeliveryMethod.HOME_DELIVERY) {
            if (request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank()) {
                throw new CheckoutException("Delivery address is required", "MISSING_ADDRESS");
            }
            if (request.getDeliveryZoneName() != null) {
                DeliveryZone zone = deliveryZoneRepository.findByName(request.getDeliveryZoneName())
                        .orElseThrow(() -> new CheckoutException("Invalid delivery zone", "INVALID_ZONE"));
                deliveryFee = zone.getDeliveryFee();
            }
        }

        // Reserve stock for each item via inventory-service
        for (CheckoutRequest.CartItem item : request.getItems()) {
            reserveStock(item.getSkuCode(), item.getQuantity(), null);
        }

        // Build order (backend calculates all prices)
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerEmail(customerEmail);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryMethod(deliveryMethod);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryCity(request.getDeliveryCity());
        order.setDeliveryZoneName(request.getDeliveryZoneName());
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        order.setPaymentMethod(paymentMethod);
        order.setNotes(request.getNotes());
        order.setDeliveryFee(deliveryFee);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CheckoutRequest.CartItem item : request.getItems()) {
            // For now, use the price provided by frontend (will be replaced with
            // catalog-service lookup when inter-service auth is set up)
            // TODO: Fetch authoritative prices from catalog-service
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSkuCode(item.getSkuCode());
            orderItem.setProductName(item.getProductName() != null ? item.getProductName() : item.getSkuCode());
            orderItem.setVariantDescription(item.getVariantDescription());
            orderItem.setUnitPrice(BigDecimal.ZERO); // Will be set from catalog lookup
            orderItem.setQuantity(item.getQuantity());
            orderItem.setLineTotal(BigDecimal.ZERO);
            order.getItems().add(orderItem);
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal.add(deliveryFee));

        Order saved = orderRepository.save(order);
        log.info("Order {} created for customer {}", saved.getOrderNumber(), customerEmail);

        return toResponse(saved);
    }

    public Page<OrderResponse> getCustomerOrders(String customerEmail, Pageable pageable) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail, pageable)
                .map(this::toResponse);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return toResponse(order);
    }

    /**
     * Customer cancellation — only allowed from PENDING or CONFIRMED states.
     * Releases reserved stock back to inventory.
     */
    @Transactional
    public OrderResponse cancelOrder(Long id, String customerEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Customers can only cancel their own orders
        if (!order.getCustomerEmail().equals(customerEmail)) {
            throw new CheckoutException("You can only cancel your own orders", "UNAUTHORIZED");
        }

        if (!order.getStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new InvalidOrderStateException(
                    "Cannot cancel order in " + order.getStatus() + " state");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Release reserved stock
        for (OrderItem item : order.getItems()) {
            try {
                releaseStock(item.getSkuCode(), item.getQuantity(), order.getOrderNumber());
            } catch (Exception e) {
                log.error("Failed to release stock for SKU {} on order cancellation: {}",
                        item.getSkuCode(), e.getMessage());
                // Continue — don't fail the cancellation if stock release fails
                // Kafka event will handle reconciliation later
            }
        }

        Order saved = orderRepository.save(order);
        log.info("Order {} cancelled by {}", order.getOrderNumber(), customerEmail);
        return toResponse(saved);
    }

    /**
     * Admin: Update order status with state machine validation.
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus targetStatus;
        try {
            targetStatus = OrderStatus.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStateException("Invalid status: " + newStatus);
        }

        if (!order.getStatus().canTransitionTo(targetStatus)) {
            throw new InvalidOrderStateException(
                    String.format("Cannot transition from %s to %s", order.getStatus(), targetStatus));
        }

        order.setStatus(targetStatus);
        Order saved = orderRepository.save(order);
        log.info("Order {} status updated to {}", order.getOrderNumber(), targetStatus);

        return toResponse(saved);
    }

    // --- Inter-service communication ---

    private void reserveStock(String skuCode, int quantity, String orderId) {
        try {
            Map<String, Object> body = Map.of(
                    "skuCode", skuCode,
                    "quantity", quantity,
                    "orderId", orderId != null ? orderId : ""
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(
                    inventoryServiceUrl + "/api/v1/inventory/reserve",
                    new HttpEntity<>(body, headers),
                    String.class
            );
        } catch (HttpClientErrorException.Conflict e) {
            throw new CheckoutException(
                    "Insufficient stock for SKU: " + skuCode, "INSUFFICIENT_STOCK");
        } catch (Exception e) {
            log.error("Failed to reserve stock for {}: {}", skuCode, e.getMessage());
            throw new CheckoutException(
                    "Unable to check stock availability", "INVENTORY_SERVICE_ERROR");
        }
    }

    private void releaseStock(String skuCode, int quantity, String orderId) {
        try {
            Map<String, Object> body = Map.of(
                    "skuCode", skuCode,
                    "quantity", quantity,
                    "orderId", orderId != null ? orderId : ""
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(
                    inventoryServiceUrl + "/api/v1/inventory/release",
                    new HttpEntity<>(body, headers),
                    String.class
            );
        } catch (Exception e) {
            log.error("Failed to release stock for {}: {}", skuCode, e.getMessage());
        }
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = orderRepository.count() + 1;
        return String.format("ORD-%s-%04d", date, count);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setStatus(order.getStatus().name());
        dto.setSubtotal(order.getSubtotal());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setTotal(order.getTotal());
        dto.setDeliveryMethod(order.getDeliveryMethod().name());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryCity(order.getDeliveryCity());
        dto.setDeliveryZoneName(order.getDeliveryZoneName());
        dto.setContactName(order.getContactName());
        dto.setContactPhone(order.getContactPhone());
        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        dto.setItems(order.getItems().stream().map(item -> {
            OrderResponse.OrderItemResponse itemDto = new OrderResponse.OrderItemResponse();
            itemDto.setId(item.getId());
            itemDto.setSkuCode(item.getSkuCode());
            itemDto.setProductName(item.getProductName());
            itemDto.setVariantDescription(item.getVariantDescription());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setLineTotal(item.getLineTotal());
            return itemDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}
