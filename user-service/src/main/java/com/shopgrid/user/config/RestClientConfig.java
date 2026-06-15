package com.shopgrid.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${auth-service.url:http://localhost:8081}")
    private String authServiceUrl;

    @Bean
    public RestClient restClient() {
        return org.springframework.web.client.RestClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }
}
