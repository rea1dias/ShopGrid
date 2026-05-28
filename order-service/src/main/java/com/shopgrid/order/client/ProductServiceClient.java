package com.shopgrid.order.client;

import com.shopgrid.order.common.dto.request.ProductInfo;
import com.shopgrid.order.common.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestClient client;
    private final CircuitBreakerFactory circuitBreakerFactory;

    private final RestClient restClient;
    public ProductInfo getProductInfo(UUID id) {
        CircuitBreaker breaker = circuitBreakerFactory.create("productService");
        return breaker.run(
                () -> restClient.get()
                        .uri("/api/products/{id}", id)
                        .retrieve()
                        .body(ProductInfo.class),
                throwable -> fallback(id, throwable)
        );
    }

    private ProductInfo fallback(UUID id, Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        throw new ServiceUnavailableException("Product service is unavailable");

    }
}
