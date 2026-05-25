package com.safo.icos.modules.workflow.approvals.api.event;

import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApprovalApprovedEvent extends ApplicationEvent {
    private final ApprovalRequest request;

    public ApprovalApprovedEvent(Object source, ApprovalRequest request) {
        super(source);
        this.request = request;
    }
}
