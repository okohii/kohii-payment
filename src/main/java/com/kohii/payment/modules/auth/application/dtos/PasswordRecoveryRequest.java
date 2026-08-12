package com.kohii.payment.modules.auth.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordRecoveryRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email is invalid")
        String email
) {
}
