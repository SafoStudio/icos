package com.safo.icos.modules.talent.onboarding.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when an onboarding task is assigned to a user.
 * Used for notifications.
 */
@Getter
public class OnboardingTaskAssignedEvent extends ApplicationEvent {
  private final UUID taskId;
  private final UUID assigneeId;
  private final String taskTitle;

  public OnboardingTaskAssignedEvent(Object source, UUID taskId,
                                     UUID assigneeId, String taskTitle) {
    super(source);
    this.taskId = taskId;
    this.assigneeId = assigneeId;
    this.taskTitle = taskTitle;
  }
}