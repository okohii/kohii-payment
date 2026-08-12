package com.kohii.payment.modules.auth.application.dtos;

public record AuthResponse(
        String token,
        String email,
        String name
) {
}
