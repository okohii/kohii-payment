package com.kohii.payment.modules.user.application.usecases;

import com.kohii.payment.modules.user.domain.entities.User;
import com.kohii.payment.modules.user.domain.repositories.UserInterfaceRepository;

public class AddUserUseCase {

    private final UserInterfaceRepository userRepository;

    public AddUserUseCase(UserInterfaceRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(User user) {
        return userRepository.addUser(user);
    }
}
