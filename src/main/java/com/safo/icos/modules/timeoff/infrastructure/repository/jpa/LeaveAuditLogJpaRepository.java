package com.safo.icos.modules.timeoff.infrastructure.repository.jpa;

import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveAuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LeaveAuditLogJpaRepository extends JpaRepository<LeaveAuditLogJpaEntity, UUID> {
    List<LeaveAuditLogJpaEntity> findByLeaveRequestId(UUID leaveRequestId);
}
