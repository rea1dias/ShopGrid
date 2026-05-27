package com.shopgrid.product.event;

import java.util.UUID;

public record ProductCreatedEvent(
        UUID productId
) {}
