package com.safo.icos.modules.onboarding.infrastructure.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingTemplateJpaRepository extends JpaRepository<OnboardingTemplateJpaEntity, UUID> {
    List<OnboardingTemplateJpaEntity> findByCompanyId(UUID companyId);
}
