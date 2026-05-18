package com.shopgrid.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestClient restClient;

    public void updateUser(String email, String firstName, String lastName) {
        restClient.put()
                .uri("/api/auth/internal/update")
                .body(new UpdateProfileRequest(email, firstName, lastName))
                .retrieve()
                .toBodilessEntity();
    }
    public record UpdateProfileRequest(String email, String firstName, String lastName) {}
}
