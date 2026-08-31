package com.smartmobilehub.notification.service;

import com.smartmobilehub.notification.entity.Notification;
import com.smartmobilehub.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationService(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    /**
     * Send an email notification.
     * Logs to database regardless of success/failure for audit trail.
     */
    public Notification sendNotification(String recipientEmail, String subject, String body,
                                          Notification.NotificationType type, String referenceId) {
        Notification notification = new Notification();
        notification.setRecipientEmail(recipientEmail);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setType(type);
        notification.setReferenceId(referenceId);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@smartmobilehub.com");

            mailSender.send(message);

            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Email sent to {} — subject: {}", recipientEmail, subject);
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage());
        }

        return notificationRepository.save(notification);
    }

    /** Send order confirmation email */
    public Notification sendOrderConfirmation(String email, String orderNumber) {
        String subject = "Order Confirmed — " + orderNumber;
        String body = String.format(
                "Dear Customer,\n\n" +
                "Your order %s has been confirmed!\n\n" +
                "You can track your order status at Smart Mobile Hub.\n\n" +
                "Thank you for shopping with us!\n" +
                "Smart Mobile Hub Team", orderNumber);
        return sendNotification(email, subject, body,
                Notification.NotificationType.ORDER_CONFIRMATION, orderNumber);
    }

    /** Send order status update email */
    public Notification sendOrderStatusUpdate(String email, String orderNumber, String newStatus) {
        String subject = "Order Update — " + orderNumber;
        String body = String.format(
                "Dear Customer,\n\n" +
                "Your order %s status has been updated to: %s\n\n" +
                "Smart Mobile Hub Team", orderNumber, newStatus);
        return sendNotification(email, subject, body,
                Notification.NotificationType.ORDER_STATUS_UPDATE, orderNumber);
    }

    /** Send payment verified email */
    public Notification sendPaymentVerified(String email, String orderNumber) {
        String subject = "Payment Verified — " + orderNumber;
        String body = String.format(
                "Dear Customer,\n\n" +
                "Your payment for order %s has been verified.\n" +
                "Your order is now being processed.\n\n" +
                "Smart Mobile Hub Team", orderNumber);
        return sendNotification(email, subject, body,
                Notification.NotificationType.PAYMENT_VERIFIED, orderNumber);
    }

    public Page<Notification> getNotificationsForUser(String email, Pageable pageable) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email, pageable);
    }

    public Page<Notification> getFailedNotifications(Pageable pageable) {
        return notificationRepository.findByStatusOrderByCreatedAtDesc(
                Notification.NotificationStatus.FAILED, pageable);
    }
}
