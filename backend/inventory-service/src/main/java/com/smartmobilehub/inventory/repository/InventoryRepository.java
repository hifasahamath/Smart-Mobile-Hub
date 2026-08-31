package com.smartmobilehub.inventory.repository;

import com.smartmobilehub.inventory.entity.InventoryRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryRecord, Long> {

    Optional<InventoryRecord> findBySkuCode(String skuCode);

    /**
     * Pessimistic write lock — prevents concurrent modifications to the same row.
     * Critical for preventing overselling during concurrent checkouts.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryRecord i WHERE i.skuCode = :skuCode")
    Optional<InventoryRecord> findBySkuCodeForUpdate(@Param("skuCode") String skuCode);

    @Query("SELECT i FROM InventoryRecord i WHERE (i.quantity - i.reservedQuantity) <= i.lowStockThreshold")
    List<InventoryRecord> findLowStockItems();

    List<InventoryRecord> findBySkuCodeIn(List<String> skuCodes);
}
