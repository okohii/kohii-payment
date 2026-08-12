package com.kohii.payment.modules.auth.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email is invalid")
        String email,

        @NotBlank(message = "password is required")
        String password
) {
}
