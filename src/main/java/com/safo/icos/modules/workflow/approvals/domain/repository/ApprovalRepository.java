package com.safo.icos.modules.workflow.approvals.domain.repository;

import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalRepository {
    ApprovalRequest save(ApprovalRequest request);
    Optional<ApprovalRequest> findById(UUID id);
}
