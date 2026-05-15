package com.shopgrid.auth.infrastructure.persistence.repository;

import com.shopgrid.auth.infrastructure.persistence.entity.AuthUser;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository extends JpaRepository<AuthUser, UUID> {

    boolean existsByEmail(String email);

    Optional<AuthUser> findByEmail(String email);
}
