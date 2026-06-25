package com.shopgrid.order.common.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record CancelItemRequest(
        @NotEmpty
        List<UUID> itemIds
) {
}
