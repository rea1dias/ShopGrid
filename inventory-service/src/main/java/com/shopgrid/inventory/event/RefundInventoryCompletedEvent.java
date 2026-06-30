package com.shopgrid.inventory.event;

import java.util.UUID;

public record RefundInventoryCompletedEvent(
        UUID eventId,
        UUID orderId
) {
}
