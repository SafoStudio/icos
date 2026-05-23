package com.safo.icos.modules.workflow.tasks.infrastructure.mapper;

import com.safo.icos.modules.workflow.tasks.domain.model.Task;
import com.safo.icos.modules.workflow.tasks.infrastructure.repository.entity.TaskJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public Task toDomain(TaskJpaEntity entity) {
        return Task.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .assigneeId(entity.getAssigneeId())
                .departmentId(entity.getDepartmentId())
                .metadata(entity.getMetadata())
                .build();
    }

    public TaskJpaEntity toEntity(Task task) {
        return TaskJpaEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .assigneeId(task.getAssigneeId())
                .departmentId(task.getDepartmentId())
                .metadata(task.getMetadata())
                .build();
    }
}
