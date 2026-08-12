package com.kohii.payment.modules.auth.api.controllers;

import com.kohii.payment.modules.auth.application.dtos.AuthResponse;
import com.kohii.payment.modules.auth.application.dtos.LoginRequest;
import com.kohii.payment.modules.auth.application.dtos.PasswordRecoveryRequest;
import com.kohii.payment.modules.auth.application.dtos.PasswordResetRequest;
import com.kohii.payment.modules.auth.application.dtos.RegisterRequest;
import com.kohii.payment.modules.auth.application.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/password-recovery/request")
    public ResponseEntity<Void> requestPasswordRecovery(@Valid @RequestBody PasswordRecoveryRequest request) {
        authService.requestPasswordRecovery(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password-recovery/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
