package com.safo.icos.modules.talent.onboarding.domain.repository;

import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link OnboardingTask} persistence.
 */
public interface OnboardingTaskRepository {
  OnboardingTask save(OnboardingTask task);
  Optional<OnboardingTask> findById(UUID id);
  List<OnboardingTask> findByProcessId(UUID processId);
  List<OnboardingTask> findByAssigneeId(UUID assigneeId);
}