package com.shopgrid.auth.application;

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

    private final RestClient restClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    public void createUser(UUID id, String firstName, String lastName, String email) {

        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("createUser");
        Retry retry = retryRegistry.retry("createUser");

        breaker.getEventPublisher()
                .onStateTransition(
                        event -> log.info("Circuit Breaker state: {} -> {}",
                                event.getStateTransition().getToState(),
                                event.getStateTransition().getFromState()));
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onError(event -> log.info("All retries failed: {}", event.getLastThrowable().getMessage()));

        Supplier<Void> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> {
                    restClient.post()
                            .uri("/api/users/internal/create")
                            .body(new CreateUserResponse(id, firstName, lastName, email))
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
        log.error("Failed to create user profile in user-service: {}", t.getMessage());
        throw new ServiceUnavailableException("Registration failed: user service is unavailable");
    }

    public record CreateUserResponse(
            UUID id,
            String firstName,
            String lastName,
            String email
    ) {
    }
}
