package com.safo.icos.modules.performance.infrastructure.repository.jpa;

import com.safo.icos.modules.performance.infrastructure.repository.entity.ObjectiveJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectiveJpaRepository extends JpaRepository<ObjectiveJpaEntity, UUID> {
    List<ObjectiveJpaEntity> findByUserId(UUID userId);
}
