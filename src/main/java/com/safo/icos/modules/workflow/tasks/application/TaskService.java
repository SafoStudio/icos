package com.safo.icos.modules.workflow.tasks.application;

import com.safo.icos.modules.workflow.tasks.api.event.TaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskStatusChangedEvent;
import com.safo.icos.modules.workflow.tasks.domain.model.Task;
import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;
import com.safo.icos.modules.workflow.tasks.domain.repository.TaskRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Task createTask(String title, String description, UUID assigneeId, UUID departmentId) {
        Task task = Task.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description(description)
                .status(TaskStatus.TODO)
                .assigneeId(assigneeId)
                .departmentId(departmentId)
                .build();
        Task savedTask = taskRepository.save(task);

        eventPublisher.publishEvent(new TaskAssignedEvent(this, savedTask.getId(), assigneeId, title));

        return savedTask;
    }

    @Transactional
    public Task moveTask(UUID taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Logic for state transition validation can be added here
        task.moveTo(newStatus);

        // Update task status and persist
        Task updatedTask = Task.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(newStatus)
                .assigneeId(task.getAssigneeId())
                .departmentId(task.getDepartmentId())
                .metadata(task.getMetadata())
                .build();

        Task savedTask = taskRepository.save(updatedTask);

        eventPublisher.publishEvent(new TaskStatusChangedEvent(this, savedTask.getId(), savedTask.getAssigneeId(), savedTask.getTitle(), newStatus));

        return savedTask;
    }
}
