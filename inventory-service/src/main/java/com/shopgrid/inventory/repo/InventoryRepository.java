package com.shopgrid.inventory.repo;

import com.shopgrid.inventory.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findById(UUID id);

    Optional<Inventory> findByProductId(UUID productId);
}
