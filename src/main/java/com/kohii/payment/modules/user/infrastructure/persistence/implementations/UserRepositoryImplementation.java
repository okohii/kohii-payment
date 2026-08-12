package com.kohii.payment.modules.user.infrastructure.persistence.implementations;

import com.kohii.payment.modules.user.domain.entities.User;
import com.kohii.payment.modules.user.domain.repositories.UserInterfaceRepository;
import com.kohii.payment.modules.user.infrastructure.persistence.entities.UserJPA;
import com.kohii.payment.modules.user.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class UserRepositoryImplementation implements UserInterfaceRepository {

    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImplementation(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User addUser(User user) {
        return userJpaRepository.save(UserJPA.fromDomain(user)).toDomain();
    }

    @Override
    public User updateUser(User user) {
        if (user == null || user.id() == null) {
            throw new IllegalArgumentException("User id is required");
        }

        userJpaRepository.findById(user.id())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.id()));

        return userJpaRepository.save(UserJPA.fromDomain(user)).toDomain();
    }

    @Override
    public void deleteUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User id is required");
        }

        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User id is required");
        }

        return userJpaRepository.findById(id)
                .map(UserJPA::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("User email is required");
        }

        return userJpaRepository.findByEmail(email)
                .map(UserJPA::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
