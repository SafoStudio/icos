package com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.onboarding.infrastructure.repository.entity.OnboardingProcessJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingProcessJpaRepository extends JpaRepository<OnboardingProcessJpaEntity, UUID> {
    Optional<OnboardingProcessJpaEntity> findByMembershipId(UUID membershipId);
}
