package com.safo.icos.modules.talent.onboarding.domain.repository;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link OnboardingTemplate} persistence.
 */
public interface OnboardingTemplateRepository {
  OnboardingTemplate save(OnboardingTemplate template);
  Optional<OnboardingTemplate> findById(UUID id);
  List<OnboardingTemplate> findByCompanyId(UUID companyId);
}