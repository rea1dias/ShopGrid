package com.shopgrid.cart.repo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopgrid.cart.domain.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartRepository {

    private static final Duration TTL = Duration.ofDays(7);
    private static final String KEY_PREFIX = "cart:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(Cart cart) {
        try {
            String key = KEY_PREFIX + cart.getUserId();
            String json = objectMapper.writeValueAsString(cart);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.info("Saved cart for user {} with {} items", cart.getUserId(), cart.getItems().size());
        } catch (Exception e) {
            log.error("Failed to serialize cart for user {}", cart.getUserId(), e);
        }
    }

    public void delete(UUID userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }

    public Cart get(UUID userId) {
        String key = KEY_PREFIX + userId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return new Cart(userId, new java.util.ArrayList<>());
        }
        try {
            return objectMapper.readValue(json, Cart.class);
        } catch (Exception e) {
            log.error("Failed to deserialize cart for user {}", userId, e);
            return new Cart(userId, new java.util.ArrayList<>());
        }
    }
}
