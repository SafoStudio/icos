package com.safo.icos.modules.workflow.tasks.domain.repository;

import com.safo.icos.modules.workflow.tasks.domain.model.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
  Task save(Task task);
  Optional<Task> findById(UUID id);
  java.util.List<Task> findByAssigneeId(UUID assigneeId);
  java.util.List<Task> findAll();
}