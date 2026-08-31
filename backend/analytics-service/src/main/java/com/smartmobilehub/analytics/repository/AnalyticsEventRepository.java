package com.smartmobilehub.analytics.repository;

import com.smartmobilehub.analytics.entity.AnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsEventRepository extends JpaRepository<AnalyticsEvent, Long> {

    long countByEventType(AnalyticsEvent.EventType eventType);

    @Query("SELECT e.eventType, COUNT(e) FROM AnalyticsEvent e " +
           "WHERE e.createdAt >= :since GROUP BY e.eventType ORDER BY COUNT(e) DESC")
    List<Object[]> countByEventTypeSince(@Param("since") LocalDateTime since);

    @Query("SELECT e.eventData, COUNT(e) FROM AnalyticsEvent e " +
           "WHERE e.eventType = 'PRODUCT_VIEW' AND e.createdAt >= :since " +
           "GROUP BY e.eventData ORDER BY COUNT(e) DESC")
    List<Object[]> getTopViewedProducts(@Param("since") LocalDateTime since);

    @Query("SELECT e.eventData, COUNT(e) FROM AnalyticsEvent e " +
           "WHERE e.eventType = 'SEARCH' AND e.createdAt >= :since " +
           "GROUP BY e.eventData ORDER BY COUNT(e) DESC")
    List<Object[]> getTopSearches(@Param("since") LocalDateTime since);
}
