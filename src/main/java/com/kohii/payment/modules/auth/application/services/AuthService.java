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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {

    private static final int RECOVERY_CODE_EXPIRATION_MINUTES = 15;

    private final UserInterfaceRepository userRepository;
    private final PasswordRecoveryCodeJpaRepository passwordRecoveryCodeJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsServiceImplementation userDetailsService;

    public AuthService(
            UserInterfaceRepository userRepository,
            PasswordRecoveryCodeJpaRepository passwordRecoveryCodeJpaRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsServiceImplementation userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordRecoveryCodeJpaRepository = passwordRecoveryCodeJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        validateEmailIsAvailable(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(UUID.randomUUID(), request.name(), request.email(), encodedPassword, true);
        User savedUser = userRepository.addUser(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.email());

        return new AuthResponse(jwtService.generateToken(userDetails), savedUser.email(), savedUser.name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.getUserByEmail(request.email());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.email());

        return new AuthResponse(jwtService.generateToken(userDetails), user.email(), user.name());
    }

    public void requestPasswordRecovery(PasswordRecoveryRequest request) {
        if (!userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        passwordRecoveryCodeJpaRepository.findByEmailAndUsedAtIsNull(request.email())
                .forEach(code -> {
                    code.setUsedAt(LocalDateTime.now());
                    passwordRecoveryCodeJpaRepository.save(code);
                });

        String recoveryCode = generateRecoveryCode();
        passwordRecoveryCodeJpaRepository.save(
                new PasswordRecoveryCodeJPA(
                        UUID.randomUUID(),
                        request.email(),
                        passwordEncoder.encode(recoveryCode),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(RECOVERY_CODE_EXPIRATION_MINUTES),
                        null
                )
        );

        sendRecoveryCodeEmail(request.email(), recoveryCode);
    }

    public void resetPassword(PasswordResetRequest request) {
        PasswordRecoveryCodeJPA code = passwordRecoveryCodeJpaRepository
                .findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recovery code"));

        if (code.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recovery code expired");
        }

        if (!passwordEncoder.matches(request.code(), code.getCodeHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid recovery code");
        }

        code.setUsedAt(LocalDateTime.now());
        passwordRecoveryCodeJpaRepository.save(code);

        User user = userRepository.getUserByEmail(request.email());
        User updatedUser = new User(
                user.id(),
                user.name(),
                user.email(),
                passwordEncoder.encode(request.newPassword()),
                user.enabled()
        );
        userRepository.updateUser(updatedUser);
    }

    private void validateEmailIsAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    private String generateRecoveryCode() {
        int code = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", code);
    }

    private void sendRecoveryCodeEmail(String email, String recoveryCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password recovery code");
        message.setText("Your password recovery code is: " + recoveryCode + "\nIt expires in " + RECOVERY_CODE_EXPIRATION_MINUTES + " minutes.");
        mailSender.send(message);
    }
}
