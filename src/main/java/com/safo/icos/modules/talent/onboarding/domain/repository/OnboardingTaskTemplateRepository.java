package com.safo.icos.modules.talent.onboarding.domain.repository;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTaskTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingTaskTemplateRepository {
    OnboardingTaskTemplate save(OnboardingTaskTemplate taskTemplate);
    Optional<OnboardingTaskTemplate> findById(UUID id);
    List<OnboardingTaskTemplate> findByTemplateId(UUID templateId);
}
