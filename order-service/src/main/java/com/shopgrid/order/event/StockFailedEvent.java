package com.shopgrid.order.event;

import java.util.UUID;

public record StockFailedEvent(
        UUID orderId,
        UUID userId,
        String reason
) {
}
