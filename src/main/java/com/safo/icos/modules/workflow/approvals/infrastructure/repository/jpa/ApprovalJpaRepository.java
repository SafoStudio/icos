package com.safo.icos.modules.workflow.approvals.infrastructure.repository.jpa;

import com.safo.icos.modules.workflow.approvals.infrastructure.repository.entity.ApprovalRequestJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalJpaRepository extends JpaRepository<ApprovalRequestJpaEntity, UUID> {
    List<ApprovalRequestJpaEntity> findAllByApproverId(UUID approverId);
}
