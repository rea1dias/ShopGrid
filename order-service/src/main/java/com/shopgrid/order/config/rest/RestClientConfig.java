package com.shopgrid.order.config.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    @Value("${user-service.url:http://localhost:8083}")
    private String userServiceUrl;

    @Value("${product-service.url:http://localhost:8083}")
    private String productServiceUrl;

    @Bean
    public RestClient restClient() {
        HttpClient httpClient = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        return RestClient
                .builder()
                .baseUrl(productServiceUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }

    @Bean
    public RestClient restUserClient() {
        HttpClient httpClient = HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        return RestClient
                .builder()
                .baseUrl(userServiceUrl)
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .build();
    }
}
