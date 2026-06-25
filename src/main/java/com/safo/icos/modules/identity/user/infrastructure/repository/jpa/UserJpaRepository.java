package com.safo.icos.modules.identity.user.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
  Optional<UserJpaEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
