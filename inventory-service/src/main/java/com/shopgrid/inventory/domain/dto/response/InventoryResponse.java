package com.shopgrid.inventory.domain.dto.response;

import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID productId,
        Integer quantity,
        Integer reserved
) {
}
