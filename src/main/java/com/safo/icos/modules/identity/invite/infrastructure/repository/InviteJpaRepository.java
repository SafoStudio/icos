package com.safo.icos.modules.identity.invite.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteJpaRepository extends JpaRepository<InviteJpaEntity, UUID> {
  Optional<InviteJpaEntity> findByToken(String token);

  boolean existsByEmail(String email);
}
