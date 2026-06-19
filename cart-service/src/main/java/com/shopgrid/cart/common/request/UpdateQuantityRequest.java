package com.shopgrid.cart.common.request;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequest(

        @Min(1)
        int quantity
) {
}
