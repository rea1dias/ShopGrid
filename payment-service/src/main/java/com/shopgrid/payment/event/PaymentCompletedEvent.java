package com.shopgrid.payment.event;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID orderId,
        UUID userId
) {
}
