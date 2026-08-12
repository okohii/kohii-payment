package com.kohii.payment.modules.user.domain.repositories;

import com.kohii.payment.modules.user.domain.entities.User;

import java.util.UUID;

public interface UserInterfaceRepository {

    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(UUID id);

    boolean existsByEmail(String email);

    User getUserById(UUID id);

    User getUserByEmail(String email);

}
