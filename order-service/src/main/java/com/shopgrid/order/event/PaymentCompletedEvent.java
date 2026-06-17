package com.shopgrid.order.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID orderId,
        UUID userId
) {
}