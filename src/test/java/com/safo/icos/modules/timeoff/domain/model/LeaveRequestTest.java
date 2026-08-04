package com.safo.icos.modules.timeoff.domain.model;

import com.safo.icos.modules.timeoff.domain.model.enums.LeaveRequestStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeaveRequestTest {

  @Test
  void shouldCreatePendingRequest() {
    UUID userId = UUID.randomUUID();
    UUID leaveTypeId = UUID.randomUUID();
    LocalDate start = LocalDate.of(2026, 8, 1);
    LocalDate end = LocalDate.of(2026, 8, 5);

    LeaveRequest request = LeaveRequest.create(userId, leaveTypeId, start, end, BigDecimal.valueOf(5), "Vacation");

    assertThat(request.getId()).isNotNull();
    assertThat(request.getUserId()).isEqualTo(userId);
    assertThat(request.getLeaveTypeId()).isEqualTo(leaveTypeId);
    assertThat(request.getStartDate()).isEqualTo(start);
    assertThat(request.getEndDate()).isEqualTo(end);
    assertThat(request.getTotalDays()).isEqualByComparingTo("5");
    assertThat(request.getReason()).isEqualTo("Vacation");
    assertThat(request.getStatus()).isEqualTo(LeaveRequestStatus.PENDING);
    assertThat(request.getCreatedAt()).isNotNull();
    assertThat(request.getApprovedAt()).isNull();
    assertThat(request.getRejectedAt()).isNull();
    assertThat(request.getCancelledAt()).isNull();
  }

  @Test
  void shouldApproveRequest() {
    LeaveRequest request = pendingRequest();

    LeaveRequest approved = request.approve();

    assertThat(approved.getStatus()).isEqualTo(LeaveRequestStatus.APPROVED);
    assertThat(approved.getApprovedAt()).isNotNull();
    assertThat(request.getStatus()).isEqualTo(LeaveRequestStatus.PENDING);
  }

  @Test
  void shouldRejectRequest() {
    LeaveRequest request = pendingRequest();

    LeaveRequest rejected = request.reject();

    assertThat(rejected.getStatus()).isEqualTo(LeaveRequestStatus.REJECTED);
    assertThat(rejected.getRejectedAt()).isNotNull();
  }

  @Test
  void shouldCancelRequest() {
    LeaveRequest request = pendingRequest();

    LeaveRequest cancelled = request.cancel();

    assertThat(cancelled.getStatus()).isEqualTo(LeaveRequestStatus.CANCELLED);
    assertThat(cancelled.getCancelledAt()).isNotNull();
  }

  private LeaveRequest pendingRequest() {
    return LeaveRequest.create(
            UUID.randomUUID(), UUID.randomUUID(),
            LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
            BigDecimal.valueOf(5), "Vacation");
  }
}