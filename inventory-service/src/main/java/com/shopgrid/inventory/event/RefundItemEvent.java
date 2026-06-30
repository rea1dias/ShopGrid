package com.shopgrid.inventory.event;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundItemEvent(
        UUID productId,
        Integer quantity,
        BigDecimal price
) {
}
