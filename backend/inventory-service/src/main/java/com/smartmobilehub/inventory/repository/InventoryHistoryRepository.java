package com.smartmobilehub.inventory.repository;

import com.smartmobilehub.inventory.entity.InventoryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    Page<InventoryHistory> findBySkuCodeOrderByCreatedAtDesc(String skuCode, Pageable pageable);
}
