package com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.onboarding.infrastructure.repository.entity.OnboardingTemplateJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingTemplateJpaRepository extends JpaRepository<OnboardingTemplateJpaEntity, UUID> {
    List<OnboardingTemplateJpaEntity> findByCompanyId(UUID companyId);
}
