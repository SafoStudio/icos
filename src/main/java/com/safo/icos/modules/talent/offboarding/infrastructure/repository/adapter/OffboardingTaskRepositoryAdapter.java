package com.safo.icos.modules.talent.offboarding.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTask;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTaskRepository;
import com.safo.icos.modules.talent.offboarding.infrastructure.mapper.OffboardingMapper;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.jpa.OffboardingTaskJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OffboardingTaskRepositoryAdapter implements OffboardingTaskRepository {
  private final OffboardingTaskJpaRepository jpaRepository;
  private final OffboardingMapper mapper;

  @Override
  public OffboardingTask save(OffboardingTask task) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(task)));
  }

  @Override
  public Optional<OffboardingTask> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<OffboardingTask> findByProcessId(UUID processId) {
    return jpaRepository.findByProcessId(processId).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<OffboardingTask> findByAssigneeId(UUID assigneeId) {
    return jpaRepository.findByAssigneeId(assigneeId).stream().map(mapper::toDomain).toList();
  }
}