package com.shopgrid.search.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId,
        String name,
        String description,
        BigDecimal price,
        String sku,
        String status,
        List<String> categories
) {
}
