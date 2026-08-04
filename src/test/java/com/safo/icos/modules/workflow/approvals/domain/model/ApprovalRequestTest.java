package com.safo.icos.modules.workflow.approvals.domain.model;

import com.safo.icos.modules.workflow.approvals.domain.model.enums.ApprovalStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApprovalRequestTest {

  @Test
  void shouldCreatePendingApproval() {
    ApprovalRequest request = ApprovalRequest.builder()
            .id(UUID.randomUUID())
            .targetEntityId("leave-123")
            .targetEntityType("LEAVE_REQUEST")
            .requesterId(UUID.randomUUID())
            .approverId(UUID.randomUUID())
            .status(ApprovalStatus.PENDING)
            .build();

    assertThat(request.getStatus()).isEqualTo(ApprovalStatus.PENDING);
    assertThat(request.getTargetEntityId()).isEqualTo("leave-123");
    assertThat(request.getTargetEntityType()).isEqualTo("LEAVE_REQUEST");
  }

  @Test
  void shouldApprovePendingRequest() {
    ApprovalRequest request = pendingRequest();

    request.approve();

    assertThat(request.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
  }

  @Test
  void shouldRejectPendingRequest() {
    ApprovalRequest request = pendingRequest();

    request.reject();

    assertThat(request.getStatus()).isEqualTo(ApprovalStatus.REJECTED);
  }

  @Test
  void shouldThrowWhenApprovingNonPending() {
    ApprovalRequest request = pendingRequest();
    request.approve();

    assertThatThrownBy(request::approve)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only pending");
  }

  @Test
  void shouldThrowWhenRejectingNonPending() {
    ApprovalRequest request = pendingRequest();
    request.reject();

    assertThatThrownBy(request::reject)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Only pending");
  }

  private ApprovalRequest pendingRequest() {
    return ApprovalRequest.builder()
            .id(UUID.randomUUID())
            .targetEntityId("leave-123")
            .targetEntityType("LEAVE_REQUEST")
            .requesterId(UUID.randomUUID())
            .approverId(UUID.randomUUID())
            .status(ApprovalStatus.PENDING)
            .build();
  }
}