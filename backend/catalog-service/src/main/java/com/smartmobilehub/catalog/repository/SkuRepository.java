package com.smartmobilehub.catalog.repository;

import com.smartmobilehub.catalog.entity.Sku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkuRepository extends JpaRepository<Sku, Long> {
    Optional<Sku> findBySkuCode(String skuCode);
    List<Sku> findByProductIdAndActiveTrue(Long productId);
    boolean existsBySkuCode(String skuCode);
}
