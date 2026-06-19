package com.shopgrid.cart.client;

import com.shopgrid.cart.common.request.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient restClient;

    public ProductInfo getProductInfo(UUID productId) {
        return restClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductInfo.class);
    }
}
