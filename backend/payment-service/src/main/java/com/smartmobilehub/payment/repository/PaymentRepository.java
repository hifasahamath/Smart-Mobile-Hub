package com.smartmobilehub.payment.repository;

import com.smartmobilehub.payment.entity.PaymentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {
    Optional<PaymentRecord> findByOrderNumber(String orderNumber);
    Optional<PaymentRecord> findByPaymentReference(String paymentReference);
    Page<PaymentRecord> findByCustomerEmailOrderByCreatedAtDesc(String email, Pageable pageable);
    Page<PaymentRecord> findByStatusOrderByCreatedAtDesc(PaymentRecord.PaymentStatus status, Pageable pageable);
}
