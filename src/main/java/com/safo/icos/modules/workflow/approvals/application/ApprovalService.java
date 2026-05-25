package com.safo.icos.modules.workflow.approvals.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.workflow.approvals.api.event.ApprovalApprovedEvent;
import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import com.safo.icos.modules.workflow.approvals.domain.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ApprovalRequest approveRequest(UUID id, UUID approverId) {
        ApprovalRequest request = approvalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Approval request not found"));

        // Валидация прав: только назначенный апрувер может подтвердить
        if (request.getApproverId() != null && !request.getApproverId().equals(approverId)) {
            throw new BusinessException("You are not authorized to approve this request", "UNAUTHORIZED");
        }

        request.approve(approverId);
        ApprovalRequest saved = approvalRepository.save(request);
        
        // Публикуем событие для интеграций
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

        request.reject(approverId);
        return approvalRepository.save(request);
    }
}
