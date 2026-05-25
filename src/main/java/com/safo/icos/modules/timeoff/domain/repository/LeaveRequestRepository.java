package com.safo.icos.modules.timeoff.domain.repository;

import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveRequestRepository {
    LeaveRequest save(LeaveRequest request);
    Optional<LeaveRequest> findById(UUID id);
    List<LeaveRequest> findByUserId(UUID userId);
    List<LeaveRequest> findByManagerId(UUID managerId); // We'll need a query for this
}
