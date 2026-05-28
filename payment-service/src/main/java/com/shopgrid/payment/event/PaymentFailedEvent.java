package com.shopgrid.payment.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID orderId,
        UUID userId,
        String reason
) {
}
