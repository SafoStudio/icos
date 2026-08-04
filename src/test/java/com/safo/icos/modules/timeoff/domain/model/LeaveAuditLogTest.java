package com.safo.icos.modules.timeoff.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeaveAuditLogTest {

  @Test
  void shouldCreateAuditLog() {
    UUID requestId = UUID.randomUUID();
    UUID actorId = UUID.randomUUID();

    LeaveAuditLog log = LeaveAuditLog.create(requestId, actorId, "REQUEST_CREATED", "Submitted");

    assertThat(log.getId()).isNotNull();
    assertThat(log.getLeaveRequestId()).isEqualTo(requestId);
    assertThat(log.getActorId()).isEqualTo(actorId);
    assertThat(log.getEventType()).isEqualTo("REQUEST_CREATED");
    assertThat(log.getMetadata()).isEqualTo("Submitted");
    assertThat(log.getCreatedAt()).isNotNull();
  }
}