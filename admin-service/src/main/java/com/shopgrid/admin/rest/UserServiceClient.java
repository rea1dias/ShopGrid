package com.shopgrid.admin.rest;

import com.shopgrid.admin.domain.dto.response.UserResponse;
import com.shopgrid.admin.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
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

    private final RestClient userServiceRestClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final HttpServletRequest request;

    private CircuitBreaker breaker;
    private Retry retry;

    @PostConstruct
    public void init() {
        breaker = circuitBreakerRegistry.circuitBreaker("userService");
        retry = retryRegistry.retry("userService");
        breaker.getEventPublisher()
                .onStateTransition(event -> log.info("Circuit Breaker state: {} -> {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()));
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onError(event -> log.info("All retries failed: {}", event.getLastThrowable().toString()));
    }

    public UserResponse getUser(UUID id, String token){
        Supplier<UserResponse> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> userServiceRestClient.get()
                        .uri("/api/users/internal/{id}", id)
                        .header("token", token)
                        .header("X-User-Id", request.getHeader("X-User-Id"))
                        .header("X-User-Email", request.getHeader("X-User-Email"))
                        .header("X-User-Role", request.getHeader("X-User-Role"))
                        .retrieve()
                        .body(UserResponse.class));
        Supplier<UserResponse> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback("getUser", e);
        }
    }

    public void blockUser(UUID id, String token) {
        Supplier<Void> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> {
                    userServiceRestClient.post()
                            .uri("/api/users/internal/block/{id}", id)
                            .header("token", token)
                            .header("X-User-Id", request.getHeader("X-User-Id"))
                            .header("X-User-Email", request.getHeader("X-User-Email"))
                            .header("X-User-Role", request.getHeader("X-User-Role"))
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                });
        Supplier<Void> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            retrySupplier.get();
        } catch (Exception e) {
            fallback("blockUser", e);
        }

    }

    private <T> T fallback(String operation, Throwable t) {
        log.error("Failed to call user-service [{}]: {}", operation, t.getMessage());
        throw new ServiceUnavailableException("User service is unavailable");
    }
}
