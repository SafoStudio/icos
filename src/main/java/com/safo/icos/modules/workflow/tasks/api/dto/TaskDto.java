package com.safo.icos.modules.workflow.tasks.api.dto;

import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a workflow task.
 */
@Getter
@Builder
public class TaskDto {
  private final UUID id;
  private final String title;
  private final String description;
  private final TaskStatus status;
  private final UUID assigneeId;
  private final UUID departmentId;
  private final Map<String, String> metadata;
}