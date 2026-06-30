package com.shopgrid.payment.event;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundItemEvent(
        UUID productId,
        Integer quantity,
        BigDecimal price
) {
}
