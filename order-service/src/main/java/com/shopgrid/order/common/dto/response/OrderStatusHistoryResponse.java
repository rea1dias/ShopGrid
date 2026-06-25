package com.shopgrid.order.common.dto.response;

import com.shopgrid.order.common.enums.OrderStatus;

import java.time.Instant;
import java.util.UUID;

public record OrderStatusHistoryResponse(

        UUID orderId,
        OrderStatus fromStatus,
        OrderStatus toStatus,
        Instant changedAt
) {
}
