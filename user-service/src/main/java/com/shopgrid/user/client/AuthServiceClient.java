package com.shopgrid.user.client;

import com.shopgrid.user.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestClient restClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    private CircuitBreaker breaker;
    private Retry retry;

    @PostConstruct
    public void init() {
        breaker = circuitBreakerRegistry.circuitBreaker("authService");
        retry = retryRegistry.retry("authService");

        breaker.getEventPublisher()
                .onStateTransition(
                        event -> log.info("Circuit Breaker state: {} -> {}",
                                event.getStateTransition().getToState(),
                                event.getStateTransition().getFromState()));
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onError(event -> log.info("All retries failed: {}", event.getLastThrowable().getMessage()));
    }

    public void updateUser(String email, String firstName, String lastName) {
        Supplier<Void> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> {
                    restClient.put()
                            .uri("/api/auth/internal/update")
                            .body(new UpdateProfileRequest(email, firstName, lastName))
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                });

        Supplier<Void> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            retrySupplier.get();
        } catch (Exception e) {
            fallback(e);
        }
    }

    public void blockUser(UUID id, String token) {
        Supplier<Void> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> {
                    restClient.post()
                            .uri("/api/auth/block/{id}", id)
                            .header("Authorization", token)
                            .retrieve()
                            .toBodilessEntity();
                    return null;
                });
        Supplier<Void> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            retrySupplier.get();
        } catch (Exception e) {
            fallback(e);
        }
    }

    private void fallback(Throwable t) {
        log.error("Failed to call auth-service: {}", t.getMessage());
        throw new ServiceUnavailableException("Auth service is unavailable");
    }

    public record UpdateProfileRequest(String email, String firstName, String lastName) {
    }
}
