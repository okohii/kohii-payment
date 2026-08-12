package com.kohii.payment.modules.auth.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email is invalid")
        String email,

        @NotBlank(message = "code is required")
        @Size(min = 6, max = 6, message = "code must have 6 digits")
        String code,

        @NotBlank(message = "new password is required")
        String newPassword
) {
}
