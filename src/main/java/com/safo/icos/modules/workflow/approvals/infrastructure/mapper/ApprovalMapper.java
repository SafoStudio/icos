package com.safo.icos.modules.workflow.approvals.infrastructure.mapper;

import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import com.safo.icos.modules.workflow.approvals.infrastructure.repository.entity.ApprovalRequestJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ApprovalMapper {
    public ApprovalRequest toDomain(ApprovalRequestJpaEntity entity) {
        return ApprovalRequest.builder()
                .id(entity.getId())
                .targetEntityId(entity.getTargetEntityId())
                .targetEntityType(entity.getTargetEntityType())
                .requesterId(entity.getRequesterId())
                .approverId(entity.getApproverId())
                .status(entity.getStatus())
                .build();
    }

    public ApprovalRequestJpaEntity toEntity(ApprovalRequest request) {
        return ApprovalRequestJpaEntity.builder()
                .id(request.getId())
                .targetEntityId(request.getTargetEntityId())
                .targetEntityType(request.getTargetEntityType())
                .requesterId(request.getRequesterId())
                .approverId(request.getApproverId())
                .status(request.getStatus())
                .build();
    }
}
