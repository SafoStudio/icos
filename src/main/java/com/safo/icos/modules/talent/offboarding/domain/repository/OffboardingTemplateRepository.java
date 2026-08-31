package com.safo.icos.modules.talent.offboarding.domain.repository;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OffboardingTemplateRepository {
  OffboardingTemplate save(OffboardingTemplate template);
  Optional<OffboardingTemplate> findById(UUID id);
  List<OffboardingTemplate> findByCompanyId(UUID companyId);
}
