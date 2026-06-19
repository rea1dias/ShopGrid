package com.shopgrid.cart.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${product-service.url}")
    private String productServiceUrl;

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl(productServiceUrl)
                .build();
    }
}
