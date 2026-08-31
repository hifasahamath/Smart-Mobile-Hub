package com.smartmobilehub.order.controller;

import com.smartmobilehub.order.dto.response.ApiResponse;
import com.smartmobilehub.order.entity.DeliveryZone;
import com.smartmobilehub.order.repository.DeliveryZoneRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery-zones")
public class DeliveryZoneController {

    private final DeliveryZoneRepository deliveryZoneRepository;

    public DeliveryZoneController(DeliveryZoneRepository deliveryZoneRepository) {
        this.deliveryZoneRepository = deliveryZoneRepository;
    }

    /** Public: list delivery zones for customers */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryZone>>> getAll() {
        List<DeliveryZone> zones = deliveryZoneRepository.findByActiveTrueOrderByNameAsc();
        return ResponseEntity.ok(ApiResponse.success("Delivery zones retrieved", zones));
    }

    /** Admin: create delivery zone */
    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryZone>> create(@RequestBody DeliveryZone zone) {
        DeliveryZone saved = deliveryZoneRepository.save(zone);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Delivery zone created", saved));
    }

    /** Admin: update delivery zone */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DeliveryZone>> update(@PathVariable Long id, @RequestBody DeliveryZone zone) {
        DeliveryZone existing = deliveryZoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery zone not found"));
        existing.setName(zone.getName());
        existing.setDeliveryFee(zone.getDeliveryFee());
        existing.setEstimatedDays(zone.getEstimatedDays());
        existing.setActive(zone.isActive());
        DeliveryZone saved = deliveryZoneRepository.save(existing);
        return ResponseEntity.ok(ApiResponse.success("Delivery zone updated", saved));
    }

    /** Admin: delete delivery zone */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        deliveryZoneRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Delivery zone deleted", null));
    }
}
