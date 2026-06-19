package com.shopgrid.cart.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    private UUID userId;
    private List<CartItem> items = new ArrayList<>();

    public BigDecimal totalPrice() {
        return items.stream()
                .map(CartItem::subTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem newItem) {
        Optional<CartItem> existing = items
                .stream()
                .filter(i -> i.getProductId().equals(newItem.getProductId()))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }
    }

    public void removeItem(UUID productId) {
        items.removeIf(i -> i.getProductId().equals(productId));
    }

    public void updateQuantity(UUID productId, int quantity) {
        items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int totalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

}
