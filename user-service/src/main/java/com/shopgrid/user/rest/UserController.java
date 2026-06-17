package com.shopgrid.user.rest;

import com.shopgrid.user.domain.model.AccountStatus;
import com.shopgrid.user.dto.request.UpdateUserRequest;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import com.shopgrid.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> get(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(userService.get(email));
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> update(@RequestHeader("X-User-Email") String email,
                                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(email, request));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<UserResponse>> get(Pageable pageable) {
        return ResponseEntity.ok().body(userService.getAll(pageable));
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id,
                                             @RequestParam AccountStatus status) {
        userService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/block/{id}")
    public ResponseEntity<Void> block(@PathVariable UUID id,
                                      @RequestHeader String token) {
        log.info("Blocking user {}", id);
        userService.block(id, token);
        return ResponseEntity.noContent().build();
    }

}
