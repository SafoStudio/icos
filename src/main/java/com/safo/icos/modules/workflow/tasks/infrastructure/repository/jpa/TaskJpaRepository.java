package com.safo.icos.modules.workflow.tasks.infrastructure.repository.jpa;

import com.safo.icos.modules.workflow.tasks.infrastructure.repository.entity.TaskJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, UUID> {

  List<TaskJpaEntity> findByAssigneeId(UUID assigneeId);

  List<TaskJpaEntity> findByDepartmentId(UUID departmentId);
}
