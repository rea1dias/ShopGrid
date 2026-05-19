package com.shopgrid.product.domain.dto.response;

import com.shopgrid.product.common.ProductStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateProductRequest(
        String name,
        String description,
        BigDecimal price,
        ProductStatus status,
        List<UUID> categoryIds
) {}
