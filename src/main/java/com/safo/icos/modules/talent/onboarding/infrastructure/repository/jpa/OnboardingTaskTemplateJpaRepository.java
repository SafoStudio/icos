package com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.onboarding.infrastructure.repository.entity.OnboardingTaskTemplateJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingTaskTemplateJpaRepository extends JpaRepository<OnboardingTaskTemplateJpaEntity, UUID> {
    List<OnboardingTaskTemplateJpaEntity> findByTemplateId(UUID templateId);
}
