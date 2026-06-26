package com.shopgrid.order.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID orderId,
        UUID userId,
        String reason
) {
}
