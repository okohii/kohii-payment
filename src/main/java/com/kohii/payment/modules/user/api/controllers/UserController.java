package com.kohii.payment.modules.user.api.controllers;

import com.kohii.payment.modules.user.api.dtos.UserResponse;
import com.kohii.payment.modules.user.domain.repositories.UserInterfaceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserInterfaceRepository userRepository;

    public UserController(UserInterfaceRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(UserResponse.from(userRepository.getUserByEmail(email)));
    }
}
