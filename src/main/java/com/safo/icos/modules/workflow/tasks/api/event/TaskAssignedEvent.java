package com.safo.icos.modules.workflow.tasks.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when a task is assigned to a user.
 */
@Getter
public class TaskAssignedEvent extends ApplicationEvent {
  private final UUID taskId;
  private final UUID assigneeId;
  private final String title;

  public TaskAssignedEvent(Object source, UUID taskId, UUID assigneeId, String title) {
    super(source);
    this.taskId = taskId;
    this.assigneeId = assigneeId;
    this.title = title;
  }
}