package com.shopgrid.inventory.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(UUID productId) {
        super("Order with id " + productId + " not found");
    }
}
