package com.shopgrid.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restClient;

    public void createUser(String firstName, String lastName, String email) {
        restClient.post()
                .uri("/api/users/internal/create")
                .body(new CreateUserResponse(firstName, lastName, email))
                .retrieve()
                .toBodilessEntity();
    }

    public record CreateUserResponse(
            String firstName,
            String lastName,
            String email
    ) {}
}
