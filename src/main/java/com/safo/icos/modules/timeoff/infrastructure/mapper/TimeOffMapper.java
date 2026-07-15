package com.safo.icos.modules.timeoff.infrastructure.mapper;

import com.safo.icos.modules.timeoff.api.dto.LeaveRequestDto;
import com.safo.icos.modules.timeoff.domain.model.LeaveAuditLog;
import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import com.safo.icos.modules.timeoff.domain.model.LeaveType;
import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveAuditLogJpaEntity;
import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveBalanceJpaEntity;
import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveRequestJpaEntity;
import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveTypeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TimeOffMapper {

    public LeaveRequest toDomain(LeaveRequestJpaEntity entity) {
        if (entity == null) return null;
        return LeaveRequest.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .leaveTypeId(entity.getLeaveTypeId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .totalDays(entity.getTotalDays())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .approvedAt(entity.getApprovedAt())
                .rejectedAt(entity.getRejectedAt())
                .cancelledAt(entity.getCancelledAt())
                .build();
    }

    public LeaveRequestJpaEntity toEntity(LeaveRequest domain) {
        if (domain == null) return null;
        return LeaveRequestJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .leaveTypeId(domain.getLeaveTypeId())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .totalDays(domain.getTotalDays())
                .reason(domain.getReason())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .approvedAt(domain.getApprovedAt())
                .rejectedAt(domain.getRejectedAt())
                .cancelledAt(domain.getCancelledAt())
                .build();
    }

    public LeaveBalance toDomain(LeaveBalanceJpaEntity entity) {
        if (entity == null) return null;
        return LeaveBalance.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .leaveTypeId(entity.getLeaveTypeId())
                .totalDays(entity.getTotalDays())
                .usedDays(entity.getUsedDays())
                .remainingDays(entity.getRemainingDays())
                .build();
    }

    public LeaveBalanceJpaEntity toEntity(LeaveBalance domain) {
        if (domain == null) return null;
        return LeaveBalanceJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .leaveTypeId(domain.getLeaveTypeId())
                .totalDays(domain.getTotalDays())
                .usedDays(domain.getUsedDays())
                .remainingDays(domain.getRemainingDays())
                .build();
    }

    public LeaveType toDomain(LeaveTypeJpaEntity entity) {
        if (entity == null) return null;
        return LeaveType.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .requiresBalance(entity.isRequiresBalance())
                .color(entity.getColor())
                .isActive(entity.isActive())
                .build();
    }

    public LeaveTypeJpaEntity toEntity(LeaveType domain) {
        if (domain == null) return null;
        return LeaveTypeJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .code(domain.getCode())
                .requiresBalance(domain.isRequiresBalance())
                .color(domain.getColor())
                .isActive(domain.isActive())
                .build();
    }

    public LeaveAuditLog toDomain(LeaveAuditLogJpaEntity entity) {
        if (entity == null) return null;
        return LeaveAuditLog.builder()
                .id(entity.getId())
                .leaveRequestId(entity.getLeaveRequestId())
                .actorId(entity.getActorId())
                .eventType(entity.getEventType())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public LeaveAuditLogJpaEntity toEntity(LeaveAuditLog domain) {
        if (domain == null) return null;
        return LeaveAuditLogJpaEntity.builder()
                .id(domain.getId())
                .leaveRequestId(domain.getLeaveRequestId())
                .actorId(domain.getActorId())
                .eventType(domain.getEventType())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public LeaveRequestDto toDto(LeaveRequest domain) {
        if (domain == null) return null;
        return LeaveRequestDto.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .leaveTypeId(domain.getLeaveTypeId())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .totalDays(domain.getTotalDays())
                .status(domain.getStatus().name())
                .reason(domain.getReason())
                .build();
    }
}
