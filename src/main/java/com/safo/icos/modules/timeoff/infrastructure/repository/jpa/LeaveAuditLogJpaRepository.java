package com.safo.icos.modules.timeoff.infrastructure.repository.jpa;

import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveAuditLogJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveAuditLogJpaRepository extends JpaRepository<LeaveAuditLogJpaEntity, UUID> {
    List<LeaveAuditLogJpaEntity> findByLeaveRequestId(UUID leaveRequestId);
}
