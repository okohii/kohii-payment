package com.kohii.payment.modules.user.infrastructure.persistence.repositories;

import com.kohii.payment.modules.user.infrastructure.persistence.entities.UserJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJPA, UUID> {

    Optional<UserJPA> findByEmail(String email);

    boolean existsByEmail(String email);

}
