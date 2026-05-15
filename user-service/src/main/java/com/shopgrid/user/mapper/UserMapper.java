package com.shopgrid.user.mapper;

import com.shopgrid.user.domain.User;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", expression = "java(com.shopgrid.user.domain.model.Role.USER)")
    @Mapping(target = "status", expression = "java(com.shopgrid.user.domain.model.AccountStatus.ACTIVE)")
    User toEntity(UserRequest request);

    UserResponse toResponse(User user);
}
