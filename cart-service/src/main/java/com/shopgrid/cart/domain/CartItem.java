package com.shopgrid.cart.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int quantity;

    public BigDecimal subTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
