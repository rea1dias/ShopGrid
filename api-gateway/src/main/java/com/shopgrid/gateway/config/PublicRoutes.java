package com.shopgrid.gateway.config;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class PublicRoutes {

    public static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/actuator/health",
            "/actuator/info",
            "/api/search",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/webjars",
            "/auth-service/v3/api-docs",
            "/user-service/v3/api-docs",
            "/product-service/v3/api-docs",
            "/order-service/v3/api-docs",
            "/payment-service/v3/api-docs",
            "/inventory-service/v3/api-docs",
            "/notification-service/v3/api-docs",
            "/search-service/v3/api-docs",
            "/cart-service/v3/api-docs",
            "/admin-service/v3/api-docs"
    );

    public boolean isPublic(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
