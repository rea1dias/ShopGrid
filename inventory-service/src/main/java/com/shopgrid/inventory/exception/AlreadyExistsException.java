package com.shopgrid.inventory.exception;

import java.util.UUID;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(UUID productId) {
        super("Product with id " + productId + " already exists");
    }
}
