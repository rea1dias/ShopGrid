package com.shopgrid.order.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestedEvent(
        UUID orderId,
        UUID userId,
        BigDecimal totalPrice
) {
}
