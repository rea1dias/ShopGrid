package com.shopgrid.admin.rest;

import com.shopgrid.admin.domain.dto.request.RejectRequest;
import com.shopgrid.admin.domain.dto.response.PageResponse;
import com.shopgrid.admin.domain.dto.response.RefundResponse;
import com.shopgrid.admin.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderServiceClient {

    private final RestClient orderServiceRestClient;
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

    public PageResponse<RefundResponse> getAllRefunds(String token) {
        Supplier<PageResponse<RefundResponse>> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> orderServiceRestClient
                        .get()
                        .uri("/api/orders/internal/refunds")
                        .header("Authorization", "Bearer " + token)
                        .header("X-User-Id", request.getHeader("X-User-Id"))
                        .header("X-User-Email", request.getHeader("X-User-Email"))
                        .header("X-User-Role", request.getHeader("X-User-Role"))
                        .retrieve()
                        .body(new ParameterizedTypeReference<PageResponse<RefundResponse>>() {
                        })
        );
        Supplier<PageResponse<RefundResponse>> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback("getAllRefunds", e);
        }
    }

    public RefundResponse getRequestedRefund(String token, UUID id) {
        Supplier<RefundResponse> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> orderServiceRestClient
                        .get()
                        .uri("/api/orders/internal/refunds/{orderId}", id)
                        .header("Authorization", "Bearer " + token)
                        .header("X-User-Id", request.getHeader("X-User-Id"))
                        .header("X-User-Email", request.getHeader("X-User-Email"))
                        .header("X-User-Role", request.getHeader("X-User-Role"))
                        .retrieve()
                        .body(RefundResponse.class)
        );
        Supplier<RefundResponse> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback("getAllRefunds", e);
        }
    }

    public RefundResponse rejectRefund(String token, UUID id, RejectRequest rejectRequest) {
        Supplier<RefundResponse> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> orderServiceRestClient
                        .post()
                        .uri("/api/orders/internal/refunds/reject/{orderId}", id)
                        .header("Authorization", "Bearer " + token)
                        .header("X-User-Id", request.getHeader("X-User-Id"))
                        .header("X-User-Email", request.getHeader("X-User-Email"))
                        .header("X-User-Role", request.getHeader("X-User-Role"))
                        .body(rejectRequest)
                        .retrieve()
                        .body(RefundResponse.class)
        );
        Supplier<RefundResponse> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback("rejectRefund", e);
        }
    }

    public RefundResponse approveRefund(String token, UUID id) {
        Supplier<RefundResponse> cbSupplier = CircuitBreaker.decorateSupplier(breaker,
                () -> orderServiceRestClient
                        .post()
                        .uri("/api/orders/internal/refunds/approve/{orderId}", id)
                        .header("Authorization", "Bearer " + token)
                        .header("X-User-Id", request.getHeader("X-User-Id"))
                        .header("X-User-Email", request.getHeader("X-User-Email"))
                        .header("X-User-Role", request.getHeader("X-User-Role"))
                        .retrieve()
                        .body(RefundResponse.class)
        );
        Supplier<RefundResponse> retrySupplier = Retry.decorateSupplier(retry, cbSupplier);
        try {
            return retrySupplier.get();
        } catch (Exception e) {
            return fallback("approveRefund", e);
        }
    }


    private <T> T fallback(String operation, Throwable t) {
        log.error("Failed to call user-service [{}]: {}", operation, t.getMessage());
        throw new ServiceUnavailableException("User service is unavailable");
    }
}
