package com.safo.icos.modules.workflow.tasks.domain.repository;

import com.safo.icos.modules.workflow.tasks.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
  Task save(Task task);
  Optional<Task> findById(UUID id);
  List<Task> findByAssigneeId(UUID assigneeId);
  List<Task> findAll();
  List<Task> findByDepartmentId(UUID departmentId);
}