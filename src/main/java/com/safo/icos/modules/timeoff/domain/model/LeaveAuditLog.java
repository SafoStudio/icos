package com.safo.icos.modules.timeoff.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing an audit log entry for leave request actions.
 */
@Getter
@Builder
public class LeaveAuditLog {
  private final UUID id;
  private final UUID leaveRequestId;
  private final UUID actorId;
  private final String eventType;
  private final String metadata;
  private final LocalDateTime createdAt;

  public static LeaveAuditLog create(UUID leaveRequestId, UUID actorId,
                                     String eventType, String metadata) {
    return LeaveAuditLog.builder()
            .id(UUID.randomUUID())
            .leaveRequestId(leaveRequestId)
            .actorId(actorId)
            .eventType(eventType)
            .metadata(metadata)
            .createdAt(LocalDateTime.now())
            .build();
  }
}