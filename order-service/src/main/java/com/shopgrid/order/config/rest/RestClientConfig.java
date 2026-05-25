package com.shopgrid.order.config.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .baseUrl("http://localhost:8083")
                .build();
    }
}
