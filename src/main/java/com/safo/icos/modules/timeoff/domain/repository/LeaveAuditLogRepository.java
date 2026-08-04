package com.safo.icos.modules.timeoff.domain.repository;

import com.safo.icos.modules.timeoff.domain.model.LeaveAuditLog;

import java.util.List;
import java.util.UUID;

public interface LeaveAuditLogRepository {
  LeaveAuditLog save(LeaveAuditLog log);
  List<LeaveAuditLog> findByLeaveRequestId(UUID leaveRequestId);
}