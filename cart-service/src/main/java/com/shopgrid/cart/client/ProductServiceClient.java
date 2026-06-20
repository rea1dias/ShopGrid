package com.shopgrid.cart.client;

import com.shopgrid.cart.common.request.ProductInfo;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.shopgrid.cart.exception.ServiceUnavailableException;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductServiceClient {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final RestClient restClient;

    private CircuitBreaker breaker;
    private Retry retry;

    @PostConstruct
    public void init() {
        breaker = circuitBreakerRegistry.circuitBreaker("cart-service");
        retry = retryRegistry.retry("cart-service");

        breaker.getEventPublisher()
                .onStateTransition(
                        event -> log.info("Circuit Breaker state: {} -> {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState())
                );
        retry.getEventPublisher()
                .onError(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onRetry(event -> log.info("All retry attempts: {}", event.getLastThrowable().toString()));
    }

    public ProductInfo getProductInfo(UUID productId) {
        Supplier<ProductInfo> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> restClient.get()
                        .uri("/api/products/{id}", productId)
                        .retrieve()
                        .body(ProductInfo.class));
        Supplier<ProductInfo> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback(productId, e);
        }
    }

    private ProductInfo fallback(UUID id, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        throw new ServiceUnavailableException("Product service is unavailable");
    }

}
