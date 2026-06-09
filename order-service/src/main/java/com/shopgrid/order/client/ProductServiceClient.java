package com.shopgrid.order.client;

import com.shopgrid.order.common.dto.request.ProductInfo;
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
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestClient restClient;
    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ProductInfo getProductInfo(UUID id) {
        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("productService");
        Retry retry = retryRegistry.retry("productService");

        breaker.getEventPublisher()
                        .onStateTransition(
                                event -> log.info("Circuit Breaker state: {} -> {}",
                                        event.getStateTransition().getFromState(),
                                        event.getStateTransition().getToState()));
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onError(event -> log.error("All retries failed: {}", event.getLastThrowable().getMessage()));

        log.info("Retry max attempts: {}", retry.getRetryConfig().getMaxAttempts());

        Supplier<ProductInfo> supplier = CircuitBreaker.decorateSupplier(breaker,
                () -> restClient.get()
                        .uri("/api/products/{id}", id)
                        .retrieve()
                        .body(ProductInfo.class));
        supplier = Retry.decorateSupplier(retry, supplier);
        try {
            return supplier.get();
        } catch (Exception e) {
            return fallback(id, e);
        }
    }

    private ProductInfo fallback(UUID id, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        throw new ServiceUnavailableException("Product service is unavailable");
    }
}
