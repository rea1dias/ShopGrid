package com.shopgrid.inventory.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemEvent(
        UUID productId,
        String productName,
        BigDecimal price,
        Integer quantity
) {}