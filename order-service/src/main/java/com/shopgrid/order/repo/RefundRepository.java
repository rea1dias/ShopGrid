package com.shopgrid.order.repo;

import com.shopgrid.order.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {

    List<Refund> findByUserId(UUID userId);
    Optional<Refund> findByOrderId(UUID orderId);
}
