package com.safo.icos.modules.onboarding.domain.repository;

import com.safo.icos.modules.onboarding.domain.model.OnboardingTask;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingTaskRepository {
    OnboardingTask save(OnboardingTask task);
    Optional<OnboardingTask> findById(UUID id);
    List<OnboardingTask> findByProcessId(UUID processId);
    List<OnboardingTask> findByAssigneeId(UUID assigneeId);
}
