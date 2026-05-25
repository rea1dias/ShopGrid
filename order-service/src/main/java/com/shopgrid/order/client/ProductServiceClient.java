package com.shopgrid.order.client;

import com.shopgrid.order.common.dto.request.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient restClient;
    public ProductInfo getProductInfo(UUID id) {
        return restClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .body(ProductInfo.class);
    }
}
