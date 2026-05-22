package com.safo.icos.modules.onboarding.domain.repository;

import com.safo.icos.modules.onboarding.domain.model.OnboardingTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingTemplateRepository {
    OnboardingTemplate save(OnboardingTemplate template);
    Optional<OnboardingTemplate> findById(UUID id);
    List<OnboardingTemplate> findByCompanyId(UUID companyId);
}
