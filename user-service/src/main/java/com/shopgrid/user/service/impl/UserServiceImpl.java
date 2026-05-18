package com.shopgrid.user.service.impl;

import com.shopgrid.user.client.AuthServiceClient;
import com.shopgrid.user.config.RestClientConfig;
import com.shopgrid.user.domain.User;
import com.shopgrid.user.domain.model.AccountStatus;
import com.shopgrid.user.dto.request.UpdateUserRequest;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import com.shopgrid.user.exception.NotFoundException;
import com.shopgrid.user.mapper.UserMapper;
import com.shopgrid.user.repo.UserRepository;
import com.shopgrid.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestClientConfig restClient;
    private final AuthServiceClient authServiceClient;

    @Override
    public UserResponse create(UserRequest request) {
        User user = userMapper.toEntity(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse get(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        authServiceClient.updateUser(email, request.firstName(), request.lastName());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getById(UUID id) {
        return userMapper.toResponse(
                userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateStatus(UUID id, AccountStatus status) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User found: {}", user.getId());
        user.setStatus(status);
    }
}
