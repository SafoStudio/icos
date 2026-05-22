package com.safo.icos.modules.onboarding.infrastructure.repository.impl;

import com.safo.icos.modules.onboarding.domain.model.OnboardingTask;
import com.safo.icos.modules.onboarding.domain.repository.OnboardingTaskRepository;
import com.safo.icos.modules.onboarding.infrastructure.mapper.OnboardingMapper;
import com.safo.icos.modules.onboarding.infrastructure.repository.jpa.OnboardingTaskJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OnboardingTaskRepositoryImpl implements OnboardingTaskRepository {
    private final OnboardingTaskJpaRepository jpaRepository;
    private final OnboardingMapper mapper;

    @Override
    public OnboardingTask save(OnboardingTask task) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(task)));
    }

    @Override
    public Optional<OnboardingTask> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<OnboardingTask> findByProcessId(UUID processId) {
        return jpaRepository.findByProcessId(processId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<OnboardingTask> findByAssigneeId(UUID assigneeId) {
        return jpaRepository.findByAssigneeId(assigneeId).stream().map(mapper::toDomain).toList();
    }
}
