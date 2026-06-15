package com.shopgrid.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${user-service.url:http://localhost:8082}")
    private String userServiceUrl;

    @Value("${product-service.url:http://localhost:8082}")
    private String productServiceUrl;

    @Bean
    public RestClient userServiceRestClient() {
        return RestClient
                .builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    @Bean
    public RestClient productServiceRestClient() {
        return RestClient
                .builder()
                .baseUrl(productServiceUrl)
                .build();
    }
}
