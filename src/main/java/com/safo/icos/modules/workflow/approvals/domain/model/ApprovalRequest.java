package com.safo.icos.modules.workflow.approvals.domain.model;

import com.safo.icos.modules.workflow.approvals.domain.model.enums.ApprovalStatus;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class ApprovalRequest {
    private final UUID id;
    private final String targetEntityId;
    private final String targetEntityType;
    private final UUID requesterId;
    private final UUID approverId;
    private final ApprovalStatus status;

    public void approve() {
        // Handle approval logic
    }

    public void reject() {
        // Handle rejection logic
    }
}
