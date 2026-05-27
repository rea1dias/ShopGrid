package com.shopgrid.inventory.domain.dto.request;

import jakarta.validation.constraints.NotNull;

public record InventoryUpdateRequest(

        @NotNull
        Integer quantity
) {
}
