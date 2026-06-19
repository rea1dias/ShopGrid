package com.shopgrid.cart.service.impl;

import com.shopgrid.cart.client.ProductServiceClient;
import com.shopgrid.cart.common.request.AddItemRequest;
import com.shopgrid.cart.common.request.ProductInfo;
import com.shopgrid.cart.common.response.CartResponse;
import com.shopgrid.cart.domain.Cart;
import com.shopgrid.cart.domain.CartItem;
import com.shopgrid.cart.repo.CartRepository;
import com.shopgrid.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    public CartResponse get(UUID userId) {
        Cart cart = cartRepository.get(userId);
        return toResponse(cart);
    }

    @Override
    public CartResponse add(UUID userId, AddItemRequest request) {
        ProductInfo productInfo = productServiceClient.getProductInfo(request.productId());
        Cart cart = cartRepository.get(userId);
        CartItem cartItem = new CartItem(
                request.productId(),
                productInfo.name(),
                productInfo.price(),
                request.quantity()
        );
        cart.addItem(cartItem);
        cartRepository.save(cart);
        log.info("Added item {} to cart for user {}", request.productId(), userId);
        return toResponse(cart);
    }

    @Override
    public CartResponse update(UUID userId, UUID productId, int quantity) {
        Cart cart = cartRepository.get(userId);
        cart.updateQuantity(productId, quantity);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    public CartResponse remove(UUID userId, UUID productId) {
        Cart cart = cartRepository.get(userId);
        cart.removeItem(productId);
        cartRepository.save(cart);
        return toResponse(cart);
    }

    @Override
    public void clear(UUID userId) {
        cartRepository.delete(userId);
        log.info("Cart cleared for user {}", userId);
    }

    private CartResponse toResponse(Cart cart) {
        return new CartResponse(cart.getUserId(), cart.getItems(), cart.totalPrice());
    }
}
