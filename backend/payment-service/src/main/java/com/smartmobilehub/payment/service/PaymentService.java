package com.smartmobilehub.payment.service;

import com.smartmobilehub.payment.entity.PaymentRecord;
import com.smartmobilehub.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Create a payment record when an order is placed.
     * Called by order-service (or via Kafka event later).
     */
    @Transactional
    public PaymentRecord createPayment(String orderNumber, String customerEmail,
                                        String paymentMethod, BigDecimal amount) {
        // Check if payment already exists for this order
        if (paymentRepository.findByOrderNumber(orderNumber).isPresent()) {
            throw new RuntimeException("Payment already exists for order: " + orderNumber);
        }

        PaymentRecord record = new PaymentRecord();
        record.setOrderNumber(orderNumber);
        record.setCustomerEmail(customerEmail);
        record.setPaymentMethod(PaymentRecord.PaymentMethod.valueOf(paymentMethod));
        record.setAmount(amount);

        PaymentRecord saved = paymentRepository.save(record);
        log.info("Payment {} created for order {}", saved.getPaymentReference(), orderNumber);
        return saved;
    }

    /**
     * Customer uploads a bank transfer receipt.
     */
    @Transactional
    public PaymentRecord uploadReceipt(String orderNumber, String receiptUrl, String customerEmail) {
        PaymentRecord record = paymentRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderNumber));

        if (!record.getCustomerEmail().equals(customerEmail)) {
            throw new RuntimeException("Unauthorized");
        }

        if (record.getPaymentMethod() != PaymentRecord.PaymentMethod.BANK_TRANSFER) {
            throw new RuntimeException("Receipt upload only for bank transfer payments");
        }

        record.setReceiptUrl(receiptUrl);
        record.setStatus(PaymentRecord.PaymentStatus.RECEIPT_UPLOADED);

        PaymentRecord saved = paymentRepository.save(record);
        log.info("Receipt uploaded for payment {}", saved.getPaymentReference());
        return saved;
    }

    /**
     * Admin verifies a bank transfer payment.
     */
    @Transactional
    public PaymentRecord verifyPayment(Long paymentId, boolean approved, String notes, String adminEmail) {
        PaymentRecord record = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        record.setStatus(approved ? PaymentRecord.PaymentStatus.VERIFIED : PaymentRecord.PaymentStatus.REJECTED);
        record.setVerificationNotes(notes);
        record.setVerifiedBy(adminEmail);
        record.setVerifiedAt(LocalDateTime.now());

        PaymentRecord saved = paymentRepository.save(record);
        log.info("Payment {} {} by admin {}", saved.getPaymentReference(),
                approved ? "verified" : "rejected", adminEmail);
        return saved;
    }

    public PaymentRecord getPaymentByOrder(String orderNumber) {
        return paymentRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderNumber));
    }

    public Page<PaymentRecord> getPendingVerifications(Pageable pageable) {
        return paymentRepository.findByStatusOrderByCreatedAtDesc(
                PaymentRecord.PaymentStatus.RECEIPT_UPLOADED, pageable);
    }

    public Page<PaymentRecord> getCustomerPayments(String customerEmail, Pageable pageable) {
        return paymentRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail, pageable);
    }
}
