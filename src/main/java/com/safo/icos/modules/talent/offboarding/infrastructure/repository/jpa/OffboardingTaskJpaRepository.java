package com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingTaskJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffboardingTaskJpaRepository extends JpaRepository<OffboardingTaskJpaEntity, UUID> {
  List<OffboardingTaskJpaEntity> findByProcessId(UUID processId);
  List<OffboardingTaskJpaEntity> findByAssigneeId(UUID assigneeId);
}