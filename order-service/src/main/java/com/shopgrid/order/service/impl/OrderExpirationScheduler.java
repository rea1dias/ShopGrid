package com.shopgrid.order.service.impl;

import com.shopgrid.order.common.enums.CancelReason;
import com.shopgrid.order.common.enums.OrderStatus;
import com.shopgrid.order.domain.Order;
import com.shopgrid.order.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderExpirationScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 300000)
    private void expireOrders() {
        Instant threshold = Instant.now().minus(15, ChronoUnit.MINUTES);
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, threshold);

        for (Order order : orders) {
            log.info("Expiring order {} — stuck in PENDING since {}", order.getId(), order.getCreatedAt());
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelReason(CancelReason.PAYMENT_TIMEOUT);
            orderRepository.save(order);
        }
        if (!orders.isEmpty()) {
            log.info("Expired {} stale orders", orders.size());
        }
    }
}
