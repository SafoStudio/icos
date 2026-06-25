package com.safo.icos.modules.workflow.approvals.domain.model;

import com.safo.icos.modules.workflow.approvals.domain.model.enums.ApprovalStatus;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApprovalRequest {
    private final UUID id;
    private final String targetEntityId;
    private final String targetEntityType;
    private final UUID requesterId;
    private final UUID approverId;
    private ApprovalStatus status;

    public void approve(UUID approverId) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved");
        }
        this.status = ApprovalStatus.APPROVED;
    }

    public void reject(UUID approverId) {
        if (this.status != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected");
        }
        this.status = ApprovalStatus.REJECTED;
    }
}
