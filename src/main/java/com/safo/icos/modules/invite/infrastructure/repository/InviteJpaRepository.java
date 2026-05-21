package com.safo.icos.modules.invite.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InviteJpaRepository extends JpaRepository<InviteJpaEntity, UUID> {
    Optional<InviteJpaEntity> findByToken(String token);
    boolean existsByEmail(String email);
}
