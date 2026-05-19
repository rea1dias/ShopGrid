package com.shopgrid.product.domain.dto.response;

import com.shopgrid.product.common.ProductStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String sku,
        ProductStatus status,
        UUID sellerId,
        List<CategoryResponse> categories,
        Instant createdAt
) {
}
