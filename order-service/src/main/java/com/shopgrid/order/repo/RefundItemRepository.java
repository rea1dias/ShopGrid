package com.shopgrid.order.repo;

import com.shopgrid.order.domain.RefundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefundItemRepository extends JpaRepository<RefundItem, UUID> {
}
