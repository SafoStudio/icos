package com.safo.icos.modules.compensation.infrastructure.repository.jpa;

import com.safo.icos.modules.compensation.infrastructure.repository.entity.CompensationRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CompensationJpaRepository extends JpaRepository<CompensationRecordJpaEntity, UUID> {
    Optional<CompensationRecordJpaEntity> findByUserId(UUID userId);
}
