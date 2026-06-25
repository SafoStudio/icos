package com.safo.icos.modules.workflow.tasks.infrastructure.repository;

import com.safo.icos.modules.workflow.tasks.domain.model.Task;
import com.safo.icos.modules.workflow.tasks.domain.repository.TaskRepository;
import com.safo.icos.modules.workflow.tasks.infrastructure.mapper.TaskMapper;
import com.safo.icos.modules.workflow.tasks.infrastructure.repository.jpa.TaskJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepository {
    private final TaskJpaRepository jpaRepository;
    private final TaskMapper mapper;

    @Override
    public Task save(Task task) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(task)));
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
