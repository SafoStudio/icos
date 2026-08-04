package com.safo.icos.modules.workflow.approvals.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.workflow.approvals.api.event.ApprovalApprovedEvent;
import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import com.safo.icos.modules.workflow.approvals.domain.repository.ApprovalRepository;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for approval workflow management.
 * Handles approval/rejection of generic requests
 * linked to target entities by type and ID.
 */
@Service
@RequiredArgsConstructor
public class ApprovalService {

  private final ApprovalRepository approvalRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional(readOnly = true)
  public List<ApprovalRequest> getApprovalsForUser(UUID userId) {
    return approvalRepository.findAllByApproverId(userId);
  }

  @Transactional
  public ApprovalRequest approveRequest(UUID id, UUID approverId) {
    ApprovalRequest request = approvalRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Approval request not found"));

    if (request.getApproverId() != null && !request.getApproverId().equals(approverId)) {
      throw new BusinessException("You are not authorized to approve this request", "UNAUTHORIZED");
    }

    request.approve();
    ApprovalRequest saved = approvalRepository.save(request);
    eventPublisher.publishEvent(new ApprovalApprovedEvent(this, saved));

    return saved;
  }

  @Transactional
  public ApprovalRequest rejectRequest(UUID id, UUID approverId) {
    ApprovalRequest request = approvalRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Approval request not found"));

    if (request.getApproverId() != null && !request.getApproverId().equals(approverId)) {
      throw new BusinessException("You are not authorized to reject this request", "UNAUTHORIZED");
    }

    request.reject();
    return approvalRepository.save(request);
  }
}