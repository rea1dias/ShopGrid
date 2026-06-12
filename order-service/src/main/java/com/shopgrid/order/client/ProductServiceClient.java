package com.shopgrid.order.client;

import com.shopgrid.order.common.dto.request.ProductInfo;
import com.shopgrid.order.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestClient restClient;
    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ProductInfo getProductInfo(UUID id) {
        CircuitBreaker breaker = circuitBreakerRegistry.circuitBreaker("productService");
        Retry retry = retryRegistry.retry("productService");
        TimeLimiter timeLimiter = timeLimiterRegistry.timeLimiter("productService");

        breaker.getEventPublisher()
                .onStateTransition(
                        event -> log.info("Circuit Breaker state: {} -> {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()));
        retry.getEventPublisher()
                .onRetry(event -> log.info("Retry attempt: {}", event.getNumberOfRetryAttempts()))
                .onError(event -> log.error("All retries failed: {}", event.getLastThrowable().getMessage()));
        timeLimiter.getEventPublisher()
                .onTimeout(event -> log.warn("ProductService call timed out after {}",
                        timeLimiter.getTimeLimiterConfig().getTimeoutDuration()));
        log.info("Retry max attempts: {}", retry.getRetryConfig().getMaxAttempts());
        Supplier<ProductInfo> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> {
                    try {
                        return TimeLimiter.decorateFutureSupplier(
                                timeLimiter,
                                () -> CompletableFuture.supplyAsync(
                                        () -> restClient.get()
                                                .uri("/api/products/{id}", id)
                                                .headers(headers -> {
                                                    ServletRequestAttributes attrs =
                                                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                                                    if (attrs != null) {
                                                        HttpServletRequest req = attrs.getRequest();
                                                        headers.add("X-User-Id", req.getHeader("X-User-Id"));
                                                        headers.add("X-User-Email", req.getHeader("X-User-Email"));
                                                        headers.add("X-User-Role", req.getHeader("X-User-Role"));
                                                    }
                                                })
                                                .retrieve()
                                                .body(ProductInfo.class),
                                        executor)
                        ).call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        Supplier<ProductInfo> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback(id, e);
        }
    }

    private ProductInfo fallback(UUID id, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        throw new ServiceUnavailableException("Product service is unavailable");
    }
}