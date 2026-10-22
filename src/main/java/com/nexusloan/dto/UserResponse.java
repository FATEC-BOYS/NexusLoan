package com.nexusloan.dto;

import java.time.Instant;
import java.util.UUID;

import com.nexusloan.entity.User;
import com.nexusloan.enums.Role;

public record UserResponse(UUID id, String name, String email, Role role, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
