package com.safo.icos.modules.workflow.tasks.infrastructure.repository.jpa;

import com.safo.icos.modules.workflow.tasks.infrastructure.repository.entity.TaskJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
    java.util.List<TaskJpaEntity> findByAssigneeId(UUID assigneeId);
}
