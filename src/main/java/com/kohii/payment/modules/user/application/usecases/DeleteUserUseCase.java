package com.kohii.payment.modules.user.application.usecases;

import com.kohii.payment.modules.user.domain.repositories.UserInterfaceRepository;

import java.util.UUID;

public class DeleteUserUseCase {

    private final UserInterfaceRepository userRepository;

    public DeleteUserUseCase(UserInterfaceRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(UUID id) {
        userRepository.deleteUserById(id);
    }
}
