package com.kohii.payment.modules.auth.application.services;

import com.kohii.payment.modules.auth.application.dtos.AuthResponse;
import com.kohii.payment.modules.auth.application.dtos.LoginRequest;
import com.kohii.payment.modules.auth.application.dtos.PasswordRecoveryRequest;
import com.kohii.payment.modules.auth.application.dtos.PasswordResetRequest;
import com.kohii.payment.modules.auth.application.dtos.RegisterRequest;
import com.kohii.payment.modules.auth.infrastructure.security.JwtService;
import com.kohii.payment.modules.auth.infrastructure.persistence.entities.PasswordRecoveryCodeJPA;
import com.kohii.payment.modules.auth.infrastructure.persistence.repositories.PasswordRecoveryCodeJpaRepository;
import com.kohii.payment.modules.user.domain.entities.User;
import com.kohii.payment.modules.user.domain.repositories.UserInterfaceRepository;
import com.kohii.payment.modules.user.infrastructure.security.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserInterfaceRepository userRepository;

    @Mock
    private PasswordRecoveryCodeJpaRepository passwordRecoveryCodeJpaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImplementation userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserAndReturnToken() {
        String rawPassword = "Abc@1234";
        String encodedPassword = "$2a$10$encodedPassword";
        String email = "john@email.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.addUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userDetailsService.loadUserByUsername(email)).thenReturn(
                org.springframework.security.core.userdetails.User.withUsername(email)
                        .password(encodedPassword)
                        .authorities("ROLE_USER")
                        .build()
        );
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token-123");

        AuthResponse result = authService.register(new RegisterRequest("John", email, rawPassword));

        assertEquals("token-123", result.token());
        assertEquals(email, result.email());
        assertEquals("John", result.name());
    }

    @Test
    void shouldSendRecoveryCodeByEmail() {
        String email = "john@email.com";
        PasswordRecoveryCodeJPA savedCode = new PasswordRecoveryCodeJPA(
                UUID.randomUUID(),
                email,
                "$2a$10$encoded-code",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                null
        );

        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(passwordRecoveryCodeJpaRepository.findByEmailAndUsedAtIsNull(email)).thenReturn(java.util.List.of());
        when(passwordEncoder.encode(anyString())).thenReturn(savedCode.getCodeHash());
        when(passwordRecoveryCodeJpaRepository.save(any(PasswordRecoveryCodeJPA.class))).thenReturn(savedCode);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        authService.requestPasswordRecovery(new PasswordRecoveryRequest(email));

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(passwordRecoveryCodeJpaRepository).save(any(PasswordRecoveryCodeJPA.class));
    }

    @Test
    void shouldAuthenticateUserAndReturnToken() {
        String email = "john@email.com";
        User user = new User(UUID.randomUUID(), "John", email, "$2a$10$encodedPassword", true);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(email, "Abc@1234"));
        when(userRepository.getUserByEmail(email)).thenReturn(user);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(
                org.springframework.security.core.userdetails.User.withUsername(email)
                        .password(user.password())
                        .authorities("ROLE_USER")
                        .build()
        );
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("login-token");

        AuthResponse result = authService.login(new LoginRequest(email, "Abc@1234"));

        assertEquals("login-token", result.token());
        assertEquals(email, result.email());
        assertEquals("John", result.name());
    }

    @Test
    void shouldRejectInvalidRecoveryCode() {
        String email = "john@email.com";
        PasswordRecoveryCodeJPA savedCode = new PasswordRecoveryCodeJPA(
                UUID.randomUUID(),
                email,
                "$2a$10$encoded-code",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                null
        );

        when(passwordRecoveryCodeJpaRepository.findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(email))
                .thenReturn(java.util.Optional.of(savedCode));
        when(passwordEncoder.matches("123456", savedCode.getCodeHash())).thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> authService.resetPassword(new PasswordResetRequest(email, "123456", "Abc@1234"))
        );
    }
}
