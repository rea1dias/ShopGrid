package com.shopgrid.order.repo;

import com.shopgrid.order.common.enums.OrderStatus;
import com.shopgrid.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserId(UUID userId, Pageable pageable);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, Instant time);
}
