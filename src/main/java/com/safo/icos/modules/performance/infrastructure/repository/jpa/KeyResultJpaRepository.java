package com.safo.icos.modules.performance.infrastructure.repository.jpa;

import com.safo.icos.modules.performance.infrastructure.repository.entity.KeyResultJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface KeyResultJpaRepository extends JpaRepository<KeyResultJpaEntity, UUID> {
    List<KeyResultJpaEntity> findByObjectiveId(UUID objectiveId);
}
