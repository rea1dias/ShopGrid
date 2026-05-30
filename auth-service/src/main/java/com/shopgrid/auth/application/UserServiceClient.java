package com.shopgrid.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restClient;

    public void createUser(UUID id, String firstName, String lastName, String email) {
        restClient.post()
                .uri("/api/users/internal/create")
                .body(new CreateUserResponse(id, firstName, lastName, email))
                .retrieve()
                .toBodilessEntity();
    }

    public record CreateUserResponse(
            UUID id,
            String firstName,
            String lastName,
            String email
    ) {}
}
