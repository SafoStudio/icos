package com.safo.icos.modules.talent.offboarding.domain.repository;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTask;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OffboardingTaskRepository {
  OffboardingTask save(OffboardingTask task);
  Optional<OffboardingTask> findById(UUID id);
  List<OffboardingTask> findByProcessId(UUID processId);
  List<OffboardingTask> findByAssigneeId(UUID assigneeId);
}