package com.shopgrid.order.client;

import com.shopgrid.order.common.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restUserClient;

    public UserResponse getUser(UUID id) {
        return restUserClient.get()
                .uri("/api/users/internal/{id}", id)
                .retrieve()
                .body(UserResponse.class);
    }
}
