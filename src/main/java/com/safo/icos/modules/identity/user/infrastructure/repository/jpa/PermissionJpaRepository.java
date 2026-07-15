package com.safo.icos.modules.identity.user.infrastructure.repository.jpa;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {
}
