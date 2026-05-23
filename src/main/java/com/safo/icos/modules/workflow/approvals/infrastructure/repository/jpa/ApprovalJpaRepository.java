package com.safo.icos.modules.workflow.approvals.infrastructure.repository.jpa;

import com.safo.icos.modules.workflow.approvals.infrastructure.repository.entity.ApprovalRequestJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ApprovalJpaRepository extends JpaRepository<ApprovalRequestJpaEntity, UUID> {
}
