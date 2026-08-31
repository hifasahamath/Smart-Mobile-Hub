package com.smartmobilehub.inventory.service;

import com.smartmobilehub.inventory.dto.request.AdjustStockRequest;
import com.smartmobilehub.inventory.dto.request.ReserveStockRequest;
import com.smartmobilehub.inventory.dto.response.InventoryResponse;
import com.smartmobilehub.inventory.entity.InventoryHistory;
import com.smartmobilehub.inventory.entity.InventoryRecord;
import com.smartmobilehub.inventory.exception.InsufficientStockException;
import com.smartmobilehub.inventory.exception.InventoryNotFoundException;
import com.smartmobilehub.inventory.repository.InventoryHistoryRepository;
import com.smartmobilehub.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository historyRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryHistoryRepository historyRepository) {
        this.inventoryRepository = inventoryRepository;
        this.historyRepository = historyRepository;
    }

    public InventoryResponse getStock(String skuCode) {
        InventoryRecord record = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new InventoryNotFoundException(skuCode));
        return toResponse(record);
    }

    public List<InventoryResponse> getStockBatch(List<String> skuCodes) {
        return inventoryRepository.findBySkuCodeIn(skuCodes).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Reserve stock during checkout.
     * Uses pessimistic locking to prevent overselling under concurrent purchases.
     *
     * @throws InsufficientStockException if available quantity < requested
     */
    @Transactional
    public InventoryResponse reserveStock(ReserveStockRequest request) {
        InventoryRecord record = inventoryRepository.findBySkuCodeForUpdate(request.getSkuCode())
                .orElseThrow(() -> new InventoryNotFoundException(request.getSkuCode()));

        int available = record.getAvailableQuantity();
        if (available < request.getQuantity()) {
            throw new InsufficientStockException(request.getSkuCode(), request.getQuantity(), available);
        }

        int beforeReserved = record.getReservedQuantity();
        record.setReservedQuantity(beforeReserved + request.getQuantity());
        inventoryRepository.save(record);

        logHistory(record.getSkuCode(), InventoryHistory.ActionType.RESERVE,
                request.getQuantity(), available, record.getAvailableQuantity(),
                request.getOrderId(), null);

        log.info("Reserved {} units of SKU {} (available: {} → {})",
                request.getQuantity(), request.getSkuCode(), available, record.getAvailableQuantity());

        return toResponse(record);
    }

    /**
     * Release reserved stock on order cancellation.
     * Also uses pessimistic locking for consistency.
     */
    @Transactional
    public InventoryResponse releaseStock(ReserveStockRequest request) {
        InventoryRecord record = inventoryRepository.findBySkuCodeForUpdate(request.getSkuCode())
                .orElseThrow(() -> new InventoryNotFoundException(request.getSkuCode()));

        int currentReserved = record.getReservedQuantity();
        int toRelease = Math.min(request.getQuantity(), currentReserved);

        int beforeAvailable = record.getAvailableQuantity();
        record.setReservedQuantity(currentReserved - toRelease);
        inventoryRepository.save(record);

        logHistory(record.getSkuCode(), InventoryHistory.ActionType.RELEASE,
                toRelease, beforeAvailable, record.getAvailableQuantity(),
                request.getOrderId(), null);

        log.info("Released {} units of SKU {} (available: {} → {})",
                toRelease, request.getSkuCode(), beforeAvailable, record.getAvailableQuantity());

        return toResponse(record);
    }

    /**
     * Admin: Set or adjust stock quantity.
     * Creates a new inventory record if one doesn't exist for this SKU.
     */
    @Transactional
    public InventoryResponse adjustStock(AdjustStockRequest request, String adminEmail) {
        InventoryRecord record = inventoryRepository.findBySkuCodeForUpdate(request.getSkuCode())
                .orElse(null);

        if (record == null) {
            // Create new inventory record
            record = new InventoryRecord();
            record.setSkuCode(request.getSkuCode());
            record.setQuantity(request.getQuantity());
            record.setLowStockThreshold(request.getLowStockThreshold());
            record = inventoryRepository.save(record);

            logHistory(record.getSkuCode(), InventoryHistory.ActionType.INITIAL_SET,
                    request.getQuantity(), 0, request.getQuantity(), null, adminEmail);

            log.info("Created inventory for SKU {} with quantity {}", request.getSkuCode(), request.getQuantity());
        } else {
            int before = record.getQuantity();
            record.setQuantity(request.getQuantity());
            record.setLowStockThreshold(request.getLowStockThreshold());
            inventoryRepository.save(record);

            logHistory(record.getSkuCode(), InventoryHistory.ActionType.ADJUST,
                    request.getQuantity() - before, before, request.getQuantity(), null, adminEmail);

            log.info("Adjusted inventory for SKU {} from {} to {}", request.getSkuCode(), before, request.getQuantity());
        }

        return toResponse(record);
    }

    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void logHistory(String skuCode, InventoryHistory.ActionType action, int change,
                            int before, int after, String referenceId, String performedBy) {
        InventoryHistory history = new InventoryHistory();
        history.setSkuCode(skuCode);
        history.setActionType(action);
        history.setQuantityChange(change);
        history.setQuantityBefore(before);
        history.setQuantityAfter(after);
        history.setReferenceId(referenceId);
        history.setPerformedBy(performedBy);
        historyRepository.save(history);
    }

    private InventoryResponse toResponse(InventoryRecord record) {
        InventoryResponse dto = new InventoryResponse();
        dto.setId(record.getId());
        dto.setSkuCode(record.getSkuCode());
        dto.setQuantity(record.getQuantity());
        dto.setReservedQuantity(record.getReservedQuantity());
        dto.setAvailableQuantity(record.getAvailableQuantity());
        dto.setLowStockThreshold(record.getLowStockThreshold());
        dto.setLowStock(record.isLowStock());
        return dto;
    }
}
