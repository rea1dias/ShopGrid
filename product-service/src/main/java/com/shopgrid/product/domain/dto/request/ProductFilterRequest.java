package com.shopgrid.product.domain.dto.request;

import com.shopgrid.product.common.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductFilterRequest(
        String name,
        UUID categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ProductStatus status
) {
}
