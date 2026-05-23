package com.safo.icos.modules.workflow.tasks.infrastructure.repository.jpa;

import com.safo.icos.modules.workflow.tasks.infrastructure.repository.entity.TaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {
}
