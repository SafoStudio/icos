package com.safo.icos.modules.identity.auth.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.auth.infrastructure.repository.entity.RefreshTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {

  Optional<RefreshTokenJpaEntity> findByTokenHash(String tokenHash);

  @Modifying
  @Query("DELETE FROM RefreshTokenJpaEntity rt WHERE rt.membershipId = :membershipId")
  void deleteByMembershipId(UUID membershipId);
}
