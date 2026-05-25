package com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.onboarding.infrastructure.repository.entity.OnboardingTaskJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingTaskJpaRepository extends JpaRepository<OnboardingTaskJpaEntity, UUID> {
    List<OnboardingTaskJpaEntity> findByProcessId(UUID processId);
    List<OnboardingTaskJpaEntity> findByAssigneeId(UUID assigneeId);
}
