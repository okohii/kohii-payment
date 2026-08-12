package com.kohii.payment.modules.user.application.usecases;

import com.kohii.payment.modules.user.domain.entities.User;
import com.kohii.payment.modules.user.domain.repositories.UserInterfaceRepository;

import java.util.UUID;

public class GetUserUseCase {

    private final UserInterfaceRepository userRepository;

    public GetUserUseCase(UserInterfaceRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UUID id) {
        return userRepository.getUserById(id);
    }

    public User execute(String email) {
        return userRepository.getUserByEmail(email);
    }

}
