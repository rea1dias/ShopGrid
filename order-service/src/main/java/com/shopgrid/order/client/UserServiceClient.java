package com.shopgrid.order.client;

import com.shopgrid.order.common.dto.response.UserResponse;
import com.shopgrid.order.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restUserClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public UserResponse getUser(UUID id) {

        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("userService");
        Retry retry = retryRegistry.retry("userService");

        breaker.getEventPublisher()
                .onStateTransition(
                        event -> log.info("Circuit Breaker state: {} -> {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()));
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onError(event -> log.error("All retries failed: {}", event.getLastThrowable().getMessage()));

        Supplier<UserResponse> supplier = CircuitBreaker.decorateSupplier(breaker,
                () -> restUserClient.get()
                        .uri("/api/users/internal/{id}", id)
                        .retrieve()
                        .body(UserResponse.class));
        supplier = Retry.decorateSupplier(retry, supplier);
        try {
            return supplier.get();
        } catch (Exception e) {
            return fallback(id, e);
        }
    }

    private UserResponse fallback(UUID id, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        throw new ServiceUnavailableException("Product service is unavailable");
    }
}
