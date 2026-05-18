package com.shopgrid.user.rest;

import com.shopgrid.user.dto.request.UpdateUserRequest;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import com.shopgrid.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
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
                                               @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(email, request));
    }

}
