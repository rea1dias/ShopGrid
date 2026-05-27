package com.shopgrid.order.common.exception;

import java.util.UUID;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(UUID userId) {
        super("Access denied: " + userId);
    }
}
