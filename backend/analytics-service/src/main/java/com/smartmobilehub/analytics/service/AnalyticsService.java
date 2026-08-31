package com.smartmobilehub.analytics.service;

import com.smartmobilehub.analytics.entity.AnalyticsEvent;
import com.smartmobilehub.analytics.repository.AnalyticsEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final AnalyticsEventRepository eventRepository;

    public AnalyticsService(AnalyticsEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /** Track a user event */
    public AnalyticsEvent trackEvent(String eventType, String userEmail,
                                      String sessionId, String eventData) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventType(AnalyticsEvent.EventType.valueOf(eventType));
        event.setUserEmail(userEmail);
        event.setSessionId(sessionId);
        event.setEventData(eventData);
        return eventRepository.save(event);
    }

    /** Get dashboard summary for the admin panel */
    public Map<String, Object> getDashboardSummary(int daysBack) {
        LocalDateTime since = LocalDateTime.now().minusDays(daysBack);

        Map<String, Object> summary = new LinkedHashMap<>();

        // Event counts by type
        List<Object[]> eventCounts = eventRepository.countByEventTypeSince(since);
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : eventCounts) {
            counts.put(row[0].toString(), (Long) row[1]);
        }
        summary.put("eventCounts", counts);

        // Top viewed products
        List<Object[]> topViewed = eventRepository.getTopViewedProducts(since);
        List<Map<String, Object>> topProducts = topViewed.stream()
                .limit(10)
                .map(row -> Map.<String, Object>of("productData", row[0], "views", row[1]))
                .collect(Collectors.toList());
        summary.put("topViewedProducts", topProducts);

        // Top searches
        List<Object[]> topSearches = eventRepository.getTopSearches(since);
        List<Map<String, Object>> searchList = topSearches.stream()
                .limit(10)
                .map(row -> Map.<String, Object>of("query", row[0], "count", row[1]))
                .collect(Collectors.toList());
        summary.put("topSearches", searchList);

        // Total events
        summary.put("totalEvents", eventRepository.count());
        summary.put("period", daysBack + " days");

        return summary;
    }
}
