package com.shopgrid.cart.controller;

import com.shopgrid.cart.common.request.AddItemRequest;
import com.shopgrid.cart.common.request.UpdateQuantityRequest;
import com.shopgrid.cart.common.response.CartResponse;
import com.shopgrid.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @GetMapping
    public ResponseEntity<CartResponse> get(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(service.get(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> add(@RequestHeader("X-User-Id") UUID userId,
                                            @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(service.add(userId, request));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> update(@RequestHeader("X-User-Id") UUID userId,
                                               @PathVariable UUID productId,
                                               @Valid @RequestBody UpdateQuantityRequest request) {
        return ResponseEntity.ok(service.update(userId, productId, request.quantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> remove(@RequestHeader("X-User-Id") UUID userId,
                                               @PathVariable UUID productId) {
        return ResponseEntity.ok(service.remove(userId, productId));
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@RequestHeader("X-User-Id") UUID userId) {
        service.clear(userId);
        return ResponseEntity.noContent().build();
    }

}
