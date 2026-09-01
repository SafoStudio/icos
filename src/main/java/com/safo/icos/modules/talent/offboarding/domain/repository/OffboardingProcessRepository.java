package com.safo.icos.modules.talent.offboarding.domain.repository;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingProcess;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OffboardingProcessRepository {
  OffboardingProcess save(OffboardingProcess process);
  Optional<OffboardingProcess> findById(UUID id);
  Optional<OffboardingProcess> findByMembershipId(UUID membershipId);
  List<OffboardingProcess> findByCompanyId(UUID companyId);
}