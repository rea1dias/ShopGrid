package com.shopgrid.user.service.impl;

import com.shopgrid.user.domain.User;
import com.shopgrid.user.domain.model.AccountStatus;
import com.shopgrid.user.domain.model.Role;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import com.shopgrid.user.mapper.UserMapper;
import com.shopgrid.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    public void createUserTest() {

        UserRequest request = new UserRequest(
                UUID.randomUUID(),
                "name",
                "lastName",
                "example@mail.ru",
                Role.USER,
                AccountStatus.ACTIVE,
                "+77716460855"
        );

        User user = new User();
        when(userMapper.toEntity(request)).thenReturn(user);

        User saved = new User();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = new UserResponse(
                saved.getId(),
                "name",
                "lastName",
                "example@mail.ru",
                Role.USER,
                AccountStatus.ACTIVE,
                "+77716460855");

        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = service.create(request);

        assertThat(result).isEqualTo(response);

        verify(userMapper).toEntity(request);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toResponse(saved);
    }
}
