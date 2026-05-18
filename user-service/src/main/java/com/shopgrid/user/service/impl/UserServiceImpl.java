package com.shopgrid.user.service.impl;

import com.shopgrid.user.client.AuthServiceClient;
import com.shopgrid.user.config.RestClientConfig;
import com.shopgrid.user.domain.User;
import com.shopgrid.user.dto.request.UpdateUserRequest;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import com.shopgrid.user.exception.NotFoundException;
import com.shopgrid.user.mapper.UserMapper;
import com.shopgrid.user.repo.AddressRepository;
import com.shopgrid.user.repo.UserRepository;
import com.shopgrid.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
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
        log.info("Looking for user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User found: {}", user.getId());
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        authServiceClient.updateUser(email, request.firstName(), request.lastName());
        return userMapper.toResponse(userRepository.save(user));
    }




}
