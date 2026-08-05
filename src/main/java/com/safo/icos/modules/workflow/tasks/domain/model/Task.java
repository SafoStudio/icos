package com.safo.icos.modules.workflow.tasks.domain.model;

import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a workflow task.
 * Supports status transitions via {@link #moveTo(TaskStatus)}.
 */
@Getter
@Builder
public class Task {
  private final UUID id;
  private final String title;
  private final String description;
  private final TaskStatus status;
  private final UUID assigneeId;
  private final UUID departmentId;
  private final Map<String, String> metadata;

  public void moveTo(TaskStatus newStatus) {
    // State transition validation can be added here
  }
}