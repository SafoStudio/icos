package com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingTaskTemplateJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OffboardingTaskTemplateJpaRepository extends JpaRepository<OffboardingTaskTemplateJpaEntity, UUID> {
  List<OffboardingTaskTemplateJpaEntity> findByTemplateId(UUID templateId);
}