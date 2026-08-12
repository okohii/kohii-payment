package com.kohii.payment.modules.auth.infrastructure.persistence.repositories;

import com.kohii.payment.modules.auth.infrastructure.persistence.entities.PasswordRecoveryCodeJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordRecoveryCodeJpaRepository extends JpaRepository<PasswordRecoveryCodeJPA, UUID> {

    List<PasswordRecoveryCodeJPA> findByEmailAndUsedAtIsNull(String email);

    Optional<PasswordRecoveryCodeJPA> findTopByEmailAndUsedAtIsNullOrderByCreatedAtDesc(String email);
}
