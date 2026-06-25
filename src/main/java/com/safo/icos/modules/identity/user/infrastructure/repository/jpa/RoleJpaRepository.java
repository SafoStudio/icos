package com.safo.icos.modules.identity.user.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.RoleJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
  Optional<RoleJpaEntity> findByName(String name);
}
