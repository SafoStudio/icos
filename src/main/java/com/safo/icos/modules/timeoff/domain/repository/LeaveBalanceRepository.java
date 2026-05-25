package com.safo.icos.modules.timeoff.domain.repository;

import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveBalanceRepository {
    LeaveBalance save(LeaveBalance balance);
    List<LeaveBalance> findByUserId(UUID userId);
    Optional<LeaveBalance> findByUserIdAndLeaveTypeId(UUID userId, UUID leaveTypeId);
}
