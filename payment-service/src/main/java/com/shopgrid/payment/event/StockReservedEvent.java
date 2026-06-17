package com.shopgrid.payment.event;

import java.math.BigDecimal;
import java.util.UUID;

public record StockReservedEvent(
        UUID orderId,
        UUID userId,
        BigDecimal totalPrice
) {
}
