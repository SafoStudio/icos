package com.safo.icos.modules.compensation.infrastructure.repository.jpa;

import com.safo.icos.modules.compensation.infrastructure.repository.entity.CompensationRecordJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompensationJpaRepository extends JpaRepository<CompensationRecordJpaEntity, UUID> {
    Optional<CompensationRecordJpaEntity> findByUserId(UUID userId);
}
