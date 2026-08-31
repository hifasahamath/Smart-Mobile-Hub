package com.smartmobilehub.order.repository;

import com.smartmobilehub.order.entity.DeliveryZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryZoneRepository extends JpaRepository<DeliveryZone, Long> {
    List<DeliveryZone> findByActiveTrueOrderByNameAsc();
    Optional<DeliveryZone> findByName(String name);
}
