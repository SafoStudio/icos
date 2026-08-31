package com.safo.icos.modules.talent.offboarding.domain.repository;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTaskTemplate;
import java.util.List;
import java.util.UUID;

public interface OffboardingTaskTemplateRepository {
  OffboardingTaskTemplate save(OffboardingTaskTemplate taskTemplate);
  List<OffboardingTaskTemplate> findByTemplateId(UUID templateId);
}