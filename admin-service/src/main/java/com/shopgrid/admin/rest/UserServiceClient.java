package com.shopgrid.admin.rest;

import com.shopgrid.admin.domain.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient userServiceRestClient;
    private final HttpServletRequest request;

    public UserResponse getUser(UUID id){
        return userServiceRestClient.get()
                .uri("/api/users/internal/{id}", id)
                .retrieve()
                .body(UserResponse.class);
    }

    public void blockUser(UUID id, String token){
        userServiceRestClient.post()
                .uri("/api/users/internal/block/{id}", id)
                .header("token", token)
                .header("X-User-Id", request.getHeader("X-User-Id"))
                .header("X-User-Email", request.getHeader("X-User-Email"))
                .header("X-User-Role", request.getHeader("X-User-Role"))
                .retrieve()
                .toBodilessEntity();
    }
}
