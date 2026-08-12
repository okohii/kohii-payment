package com.kohii.payment.modules.user.api.dtos;

import com.kohii.payment.modules.user.domain.entities.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Boolean enabled
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.id(), user.name(), user.email(), user.enabled());
    }
}
