package com.smartmobilehub.analytics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Tracks user events for analytics: page views, product views, searches, add-to-cart, purchases.
 */
@Entity
@Table(name = "analytics_events")
public class AnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    private String userEmail;
    private String sessionId;

    /** Event-specific data (JSON string): productId, searchQuery, etc. */
    @Column(columnDefinition = "TEXT")
    private String eventData;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum EventType {
        PAGE_VIEW,
        PRODUCT_VIEW,
        SEARCH,
        ADD_TO_CART,
        REMOVE_FROM_CART,
        CHECKOUT_START,
        ORDER_PLACED,
        ORDER_CANCELLED
    }

    public AnalyticsEvent() {}

    public Long getId() { return id; }
    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
