package com.safo.icos.modules.timeoff.infrastructure.repository.jpa;

import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveBalanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveBalanceJpaRepository extends JpaRepository<LeaveBalanceJpaEntity, UUID> {
    List<LeaveBalanceJpaEntity> findByUserId(UUID userId);
    Optional<LeaveBalanceJpaEntity> findByUserIdAndLeaveTypeId(UUID userId, UUID leaveTypeId);
}
