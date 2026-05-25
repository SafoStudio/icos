package com.safo.icos.modules.timeoff.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestApprovedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestCreatedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestRejectedEvent;
import com.safo.icos.modules.timeoff.domain.model.*;
import com.safo.icos.modules.timeoff.domain.model.enums.LeaveRequestStatus;
import com.safo.icos.modules.timeoff.domain.repository.*;
import com.safo.icos.modules.user.api.UserProvider;
import com.safo.icos.modules.user.api.dto.MembershipDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRequestRepository requestRepository;
    private final LeaveBalanceRepository balanceRepository;
    private final LeaveTypeRepository typeRepository;
    private final LeaveAuditLogRepository auditLogRepository;
    private final UserProvider userProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LeaveRequest createRequest(UUID userId, UUID leaveTypeId, LocalDate startDate, LocalDate endDate, String reason) {
        LeaveType type = typeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new NotFoundException("Leave type not found"));

        if (!type.isActive()) {
            throw new BusinessException("Leave type is not active");
        }

        BigDecimal totalDays = BigDecimal.valueOf(ChronoUnit.DAYS.between(startDate, endDate) + 1);
        
        // Basic validation
        if (totalDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("End date must be after or equal to start date");
        }

        // Check balance if required
        if (type.requiresBalance()) {
            LeaveBalance balance = balanceRepository.findByUserIdAndLeaveTypeId(userId, leaveTypeId)
                    .orElseThrow(() -> new BusinessException("No leave balance found for this type"));
            
            if (balance.getRemainingDays().compareTo(totalDays) < 0) {
                throw new BusinessException("Insufficient leave balance. Remaining: " + balance.getRemainingDays());
            }
        }

        LeaveRequest request = LeaveRequest.create(userId, leaveTypeId, startDate, endDate, totalDays, reason);
        LeaveRequest saved = requestRepository.save(request);

        auditLogRepository.save(LeaveAuditLog.create(saved.getId(), userId, "REQUEST_CREATED", "Request submitted for " + totalDays + " days"));
        
        eventPublisher.publishEvent(new LeaveRequestCreatedEvent(this, saved));

        return saved;
    }

    @Transactional
    public LeaveRequest approveRequest(UUID requestId, UUID approverId) {
        LeaveRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Leave request not found"));

        if (request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new BusinessException("Only pending requests can be approved");
        }

        // Verify approver is the manager
        MembershipDto membership = userProvider.findFirstActiveMembershipByUserId(request.getUserId())
                .orElseThrow(() -> new BusinessException("Employee membership not found"));
        
        if (!approverId.equals(membership.getManagerId())) {
            // In a real system, we might allow HR/Admin to approve too
            // For MVP, sticking to manager
            // throw new BusinessException("Only direct manager can approve this request");
        }

        LeaveRequest approved = request.approve();
        
        // Deduct balance if required
        LeaveType type = typeRepository.findById(approved.getLeaveTypeId()).orElseThrow();
        if (type.requiresBalance()) {
            LeaveBalance balance = balanceRepository.findByUserIdAndLeaveTypeId(request.getUserId(), request.getLeaveTypeId())
                    .orElseThrow();
            balanceRepository.save(balance.deduct(request.getTotalDays()));
        }

        LeaveRequest saved = requestRepository.save(approved);
        auditLogRepository.save(LeaveAuditLog.create(saved.getId(), approverId, "REQUEST_APPROVED", "Request approved by manager"));
        
        eventPublisher.publishEvent(new LeaveRequestApprovedEvent(this, saved));

        return saved;
    }

    @Transactional
    public LeaveRequest rejectRequest(UUID requestId, UUID approverId, String comment) {
        LeaveRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Leave request not found"));

        if (request.getStatus() != LeaveRequestStatus.PENDING) {
            throw new BusinessException("Only pending requests can be rejected");
        }

        LeaveRequest rejected = request.reject();
        LeaveRequest saved = requestRepository.save(rejected);

        auditLogRepository.save(LeaveAuditLog.create(saved.getId(), approverId, "REQUEST_REJECTED", "Rejected: " + comment));
        
        eventPublisher.publishEvent(new LeaveRequestRejectedEvent(this, saved));

        return saved;
    }

    @Transactional
    public LeaveRequest cancelRequest(UUID requestId, UUID userId) {
        LeaveRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Leave request not found"));

        if (!request.getUserId().equals(userId)) {
            throw new BusinessException("You can only cancel your own requests");
        }

        if (request.getStatus() == LeaveRequestStatus.CANCELLED || request.getStatus() == LeaveRequestStatus.REJECTED) {
            throw new BusinessException("Request already in terminal state");
        }

        boolean wasApproved = request.getStatus() == LeaveRequestStatus.APPROVED;
        LeaveRequest cancelled = request.cancel();

        // Restore balance if it was already deducted
        if (wasApproved) {
            LeaveType type = typeRepository.findById(request.getLeaveTypeId()).orElseThrow();
            if (type.requiresBalance()) {
                LeaveBalance balance = balanceRepository.findByUserIdAndLeaveTypeId(request.getUserId(), request.getLeaveTypeId())
                        .orElseThrow();
                balanceRepository.save(balance.restore(request.getTotalDays()));
            }
        }

        LeaveRequest saved = requestRepository.save(cancelled);
        auditLogRepository.save(LeaveAuditLog.create(saved.getId(), userId, "REQUEST_CANCELLED", "Cancelled by employee"));
        
        return saved;
    }

    public List<LeaveRequest> getMyRequests(UUID userId) {
        return requestRepository.findByUserId(userId);
    }

    public List<LeaveBalance> getMyBalances(UUID userId) {
        return balanceRepository.findByUserId(userId);
    }
}
