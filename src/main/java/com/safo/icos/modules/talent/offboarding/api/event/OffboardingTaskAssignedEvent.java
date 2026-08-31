package com.safo.icos.modules.talent.offboarding.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when an offboarding task is assigned to a user.
 * Used for notifications.
 */
@Getter
public class OffboardingTaskAssignedEvent extends ApplicationEvent {
  private final UUID taskId;
  private final UUID assigneeId;
  private final String taskTitle;

  public OffboardingTaskAssignedEvent(Object source, UUID taskId, UUID assigneeId, String taskTitle) {
    super(source);
    this.taskId = taskId;
    this.assigneeId = assigneeId;
    this.taskTitle = taskTitle;
  }
}