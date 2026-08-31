package com.smartmobilehub.notification.controller;

import com.smartmobilehub.notification.entity.Notification;
import com.smartmobilehub.notification.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Internal: Send notification (called by other services) */
    @PostMapping("/send")
    public ResponseEntity<Notification> sendNotification(@RequestBody Map<String, String> body) {
        String type = body.getOrDefault("type", "ORDER_CONFIRMATION");
        Notification result = notificationService.sendNotification(
                body.get("email"), body.get("subject"), body.get("body"),
                Notification.NotificationType.valueOf(type), body.get("referenceId"));
        return ResponseEntity.ok(result);
    }

    /** Internal: Send order confirmation */
    @PostMapping("/order-confirmation")
    public ResponseEntity<Notification> sendOrderConfirmation(@RequestBody Map<String, String> body) {
        Notification result = notificationService.sendOrderConfirmation(
                body.get("email"), body.get("orderNumber"));
        return ResponseEntity.ok(result);
    }

    /** Internal: Send order status update */
    @PostMapping("/order-status")
    public ResponseEntity<Notification> sendOrderStatusUpdate(@RequestBody Map<String, String> body) {
        Notification result = notificationService.sendOrderStatusUpdate(
                body.get("email"), body.get("orderNumber"), body.get("status"));
        return ResponseEntity.ok(result);
    }

    /** Customer: Get my notifications */
    @GetMapping
    public ResponseEntity<Page<Notification>> getMyNotifications(
            @RequestHeader("X-User-Email") String email,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(email, pageable));
    }

    /** Admin: Get failed notifications */
    @GetMapping("/failed")
    public ResponseEntity<Page<Notification>> getFailedNotifications(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(notificationService.getFailedNotifications(pageable));
    }
}
