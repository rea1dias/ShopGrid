package com.shopgrid.order.common.dto.response;

import com.shopgrid.order.common.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID userId,
        OrderStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items
) {}
