package com.smartmobilehub.order.config;

import com.smartmobilehub.order.entity.DeliveryZone;
import com.smartmobilehub.order.repository.DeliveryZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDeliveryZones(DeliveryZoneRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                log.info("Delivery zones already seeded.");
                return;
            }

            log.info("Seeding delivery zones...");

            createZone(repo, "Colombo", new BigDecimal("250.00"), 1);
            createZone(repo, "Gampaha", new BigDecimal("350.00"), 2);
            createZone(repo, "Kandy", new BigDecimal("450.00"), 3);
            createZone(repo, "Galle", new BigDecimal("500.00"), 3);
            createZone(repo, "Jaffna", new BigDecimal("600.00"), 4);
            createZone(repo, "Kurunegala", new BigDecimal("400.00"), 2);
            createZone(repo, "Matara", new BigDecimal("550.00"), 3);
            createZone(repo, "Anuradhapura", new BigDecimal("500.00"), 3);
            createZone(repo, "Batticaloa", new BigDecimal("600.00"), 4);
            createZone(repo, "Trincomalee", new BigDecimal("550.00"), 4);

            log.info("Seeded {} delivery zones", repo.count());
        };
    }

    private void createZone(DeliveryZoneRepository repo, String name, BigDecimal fee, int days) {
        DeliveryZone zone = new DeliveryZone();
        zone.setName(name);
        zone.setDeliveryFee(fee);
        zone.setEstimatedDays(days);
        repo.save(zone);
    }
}
