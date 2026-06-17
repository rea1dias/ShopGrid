package com.shopgrid.payment.repo;

import com.shopgrid.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findById(UUID id);

    boolean existsByOrderId(UUID orderId);
}
