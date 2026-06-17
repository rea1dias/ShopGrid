package com.shopgrid.inventory.event;

import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId
) {
}
