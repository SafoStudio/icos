package com.safo.icos.modules.talent.onboarding.domain.repository;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingProcess;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingProcessRepository {
    OnboardingProcess save(OnboardingProcess process);
    Optional<OnboardingProcess> findById(UUID id);
    Optional<OnboardingProcess> findByMembershipId(UUID membershipId);
}
