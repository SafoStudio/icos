package com.safo.icos.modules.timeoff.api.event;

import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when a leave request is approved.
 */
@Getter
public class LeaveRequestApprovedEvent extends ApplicationEvent {
  private final transient LeaveRequest leaveRequest;

  public LeaveRequestApprovedEvent(Object source, LeaveRequest leaveRequest) {
    super(source);
    this.leaveRequest = leaveRequest;
  }
}