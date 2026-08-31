package com.smartmobilehub.inventory.service;

import com.smartmobilehub.inventory.dto.request.AdjustStockRequest;
import com.smartmobilehub.inventory.dto.request.ReserveStockRequest;
import com.smartmobilehub.inventory.dto.response.InventoryResponse;
import com.smartmobilehub.inventory.entity.InventoryRecord;
import com.smartmobilehub.inventory.exception.InsufficientStockException;
import com.smartmobilehub.inventory.exception.InventoryNotFoundException;
import com.smartmobilehub.inventory.repository.InventoryHistoryRepository;
import com.smartmobilehub.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private InventoryHistoryRepository historyRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private InventoryRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = new InventoryRecord();
        testRecord.setSkuCode("IPH15PM-256-NAT");
        testRecord.setQuantity(100);
        testRecord.setReservedQuantity(10);
        testRecord.setLowStockThreshold(5);
    }

    @Test
    void getStock_found() {
        when(inventoryRepository.findBySkuCode("IPH15PM-256-NAT")).thenReturn(Optional.of(testRecord));

        InventoryResponse result = inventoryService.getStock("IPH15PM-256-NAT");

        assertEquals("IPH15PM-256-NAT", result.getSkuCode());
        assertEquals(100, result.getQuantity());
        assertEquals(10, result.getReservedQuantity());
        assertEquals(90, result.getAvailableQuantity());
    }

    @Test
    void getStock_notFound_throwsException() {
        when(inventoryRepository.findBySkuCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> inventoryService.getStock("INVALID"));
    }

    @Test
    void reserveStock_success() {
        when(inventoryRepository.findBySkuCodeForUpdate("IPH15PM-256-NAT"))
                .thenReturn(Optional.of(testRecord));
        when(inventoryRepository.save(any())).thenReturn(testRecord);

        ReserveStockRequest request = new ReserveStockRequest();
        request.setSkuCode("IPH15PM-256-NAT");
        request.setQuantity(5);
        request.setOrderId("ORD-001");

        InventoryResponse result = inventoryService.reserveStock(request);

        assertEquals(15, testRecord.getReservedQuantity()); // 10 + 5
        verify(historyRepository).save(any());
    }

    @Test
    void reserveStock_insufficientStock_throwsException() {
        testRecord.setQuantity(10);
        testRecord.setReservedQuantity(8); // only 2 available
        when(inventoryRepository.findBySkuCodeForUpdate("IPH15PM-256-NAT"))
                .thenReturn(Optional.of(testRecord));

        ReserveStockRequest request = new ReserveStockRequest();
        request.setSkuCode("IPH15PM-256-NAT");
        request.setQuantity(5); // requesting 5, only 2 available

        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
                () -> inventoryService.reserveStock(request));

        assertEquals(5, ex.getRequested());
        assertEquals(2, ex.getAvailable());
    }

    @Test
    void releaseStock_success() {
        when(inventoryRepository.findBySkuCodeForUpdate("IPH15PM-256-NAT"))
                .thenReturn(Optional.of(testRecord));
        when(inventoryRepository.save(any())).thenReturn(testRecord);

        ReserveStockRequest request = new ReserveStockRequest();
        request.setSkuCode("IPH15PM-256-NAT");
        request.setQuantity(5);
        request.setOrderId("ORD-001");

        inventoryService.releaseStock(request);

        assertEquals(5, testRecord.getReservedQuantity()); // 10 - 5
    }

    @Test
    void adjustStock_createsNewRecord() {
        when(inventoryRepository.findBySkuCodeForUpdate("NEW-SKU"))
                .thenReturn(Optional.empty());
        when(inventoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdjustStockRequest request = new AdjustStockRequest();
        request.setSkuCode("NEW-SKU");
        request.setQuantity(50);

        InventoryResponse result = inventoryService.adjustStock(request, "admin@test.com");

        verify(inventoryRepository).save(any());
        verify(historyRepository).save(any());
    }

    @Test
    void adjustStock_updatesExisting() {
        when(inventoryRepository.findBySkuCodeForUpdate("IPH15PM-256-NAT"))
                .thenReturn(Optional.of(testRecord));
        when(inventoryRepository.save(any())).thenReturn(testRecord);

        AdjustStockRequest request = new AdjustStockRequest();
        request.setSkuCode("IPH15PM-256-NAT");
        request.setQuantity(200);

        inventoryService.adjustStock(request, "admin@test.com");

        assertEquals(200, testRecord.getQuantity());
    }
}
