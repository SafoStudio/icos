package com.safo.icos.modules.workflow.approvals.api.event;

import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when an approval request is approved.
 */
@Getter
public class ApprovalApprovedEvent extends ApplicationEvent {
  private final transient ApprovalRequest request;

  public ApprovalApprovedEvent(Object source, ApprovalRequest request) {
    super(source);
    this.request = request;
  }
}