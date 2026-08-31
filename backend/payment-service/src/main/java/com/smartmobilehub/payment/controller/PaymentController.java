package com.smartmobilehub.payment.controller;

import com.smartmobilehub.payment.dto.response.ApiResponse;
import com.smartmobilehub.payment.entity.PaymentRecord;
import com.smartmobilehub.payment.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Create payment (internal — called by order-service) */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentRecord>> createPayment(@RequestBody Map<String, Object> body) {
        String orderNumber = (String) body.get("orderNumber");
        String customerEmail = (String) body.get("customerEmail");
        String paymentMethod = (String) body.get("paymentMethod");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        PaymentRecord record = paymentService.createPayment(orderNumber, customerEmail, paymentMethod, amount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment created", record));
    }

    /** Customer uploads bank transfer receipt */
    @PostMapping("/receipt")
    public ResponseEntity<ApiResponse<PaymentRecord>> uploadReceipt(
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Email") String customerEmail) {
        PaymentRecord record = paymentService.uploadReceipt(
                body.get("orderNumber"), body.get("receiptUrl"), customerEmail);
        return ResponseEntity.ok(ApiResponse.success("Receipt uploaded", record));
    }

    /** Get payment for an order */
    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<ApiResponse<PaymentRecord>> getByOrder(@PathVariable String orderNumber) {
        PaymentRecord record = paymentService.getPaymentByOrder(orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved", record));
    }

    /** Admin: verify or reject a payment */
    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<PaymentRecord>> verifyPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader(value = "X-User-Email", required = false) String adminEmail) {
        boolean approved = Boolean.parseBoolean(body.get("approved").toString());
        String notes = body.get("notes") != null ? body.get("notes").toString() : "";
        PaymentRecord record = paymentService.verifyPayment(id, approved, notes, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Payment " + (approved ? "verified" : "rejected"), record));
    }

    /** Admin: get pending verifications */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<PaymentRecord>>> getPending(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentRecord> records = paymentService.getPendingVerifications(pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending verifications", records));
    }

    /** Customer: get my payments */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentRecord>>> getMyPayments(
            @RequestHeader("X-User-Email") String customerEmail,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentRecord> records = paymentService.getCustomerPayments(customerEmail, pageable);
        return ResponseEntity.ok(ApiResponse.success("Payments retrieved", records));
    }
}
