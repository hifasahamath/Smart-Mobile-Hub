package com.smartmobilehub.analytics.controller;

import com.smartmobilehub.analytics.entity.AnalyticsEvent;
import com.smartmobilehub.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /** Track an event (public — called from frontend) */
    @PostMapping("/track")
    public ResponseEntity<Map<String, Object>> trackEvent(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail) {
        AnalyticsEvent event = analyticsService.trackEvent(
                body.get("eventType"),
                userEmail,
                body.get("sessionId"),
                body.get("eventData")
        );
        return ResponseEntity.ok(Map.of("success", true, "eventId", event.getId()));
    }

    /** Admin dashboard summary */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> summary = analyticsService.getDashboardSummary(days);
        return ResponseEntity.ok(Map.of("success", true, "data", summary));
    }
}
