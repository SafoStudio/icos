package com.safo.icos.modules.timeoff.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestCreatedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestRejectedEvent;
import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import com.safo.icos.modules.timeoff.domain.model.LeaveType;
import com.safo.icos.modules.timeoff.domain.model.enums.LeaveRequestStatus;
import com.safo.icos.modules.timeoff.domain.repository.LeaveAuditLogRepository;
import com.safo.icos.modules.timeoff.domain.repository.LeaveBalanceRepository;
import com.safo.icos.modules.timeoff.domain.repository.LeaveRequestRepository;
import com.safo.icos.modules.timeoff.domain.repository.LeaveTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

  @Mock private LeaveRequestRepository requestRepository;
  @Mock private LeaveBalanceRepository balanceRepository;
  @Mock private LeaveTypeRepository typeRepository;
  @Mock private LeaveAuditLogRepository auditLogRepository;
  @Mock private UserProvider userProvider;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private LeaveService leaveService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID LEAVE_TYPE_ID = UUID.randomUUID();
  private static final UUID APPROVER_ID = UUID.randomUUID();
  private static final UUID REQUEST_ID = UUID.randomUUID();

  @Test
  void shouldCreateLeaveRequest() {
    LeaveType type = LeaveType.builder()
            .id(LEAVE_TYPE_ID).name("Vacation").code("VAC")
            .requiresBalance(true).isActive(true).build();
    LeaveBalance balance = LeaveBalance.builder()
            .id(UUID.randomUUID()).userId(USER_ID).leaveTypeId(LEAVE_TYPE_ID)
            .totalDays(BigDecimal.valueOf(20)).usedDays(BigDecimal.valueOf(0))
            .remainingDays(BigDecimal.valueOf(20)).build();

    when(typeRepository.findById(LEAVE_TYPE_ID)).thenReturn(Optional.of(type));
    when(balanceRepository.findByUserIdAndLeaveTypeId(USER_ID, LEAVE_TYPE_ID)).thenReturn(Optional.of(balance));
    when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    LeaveRequest result = leaveService.createRequest(USER_ID, LEAVE_TYPE_ID,
            LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5), "Vacation");

    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.PENDING);
    assertThat(result.getTotalDays()).isEqualByComparingTo("5");
    verify(eventPublisher).publishEvent(any(LeaveRequestCreatedEvent.class));
  }

  @Test
  void shouldThrowWhenInsufficientBalance() {
    LeaveType type = LeaveType.builder()
            .id(LEAVE_TYPE_ID).name("Vacation").code("VAC")
            .requiresBalance(true).isActive(true).build();
    LeaveBalance balance = LeaveBalance.builder()
            .id(UUID.randomUUID()).userId(USER_ID).leaveTypeId(LEAVE_TYPE_ID)
            .totalDays(BigDecimal.valueOf(20)).usedDays(BigDecimal.valueOf(19))
            .remainingDays(BigDecimal.valueOf(1)).build();

    when(typeRepository.findById(LEAVE_TYPE_ID)).thenReturn(Optional.of(type));
    when(balanceRepository.findByUserIdAndLeaveTypeId(USER_ID, LEAVE_TYPE_ID)).thenReturn(Optional.of(balance));

    LocalDate startDate = LocalDate.of(2026, 8, 1);
    LocalDate endDate = LocalDate.of(2026, 8, 5);

    assertThatThrownBy(() -> leaveService.createRequest(USER_ID, LEAVE_TYPE_ID, startDate, endDate, "Vacation"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Insufficient");
  }

  @Test
  void shouldRejectLeaveRequest() {
    LeaveRequest pending = LeaveRequest.create(USER_ID, LEAVE_TYPE_ID,
            LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
            BigDecimal.valueOf(5), "Vacation");

    when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));
    when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    LeaveRequest result = leaveService.rejectRequest(REQUEST_ID, APPROVER_ID, "Not allowed");

    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.REJECTED);
    verify(eventPublisher).publishEvent(any(LeaveRequestRejectedEvent.class));
  }

  @Test
  void shouldThrowWhenRejectingNonPending() {
    LeaveRequest approved = LeaveRequest.create(USER_ID, LEAVE_TYPE_ID,
                    LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                    BigDecimal.valueOf(5), "Vacation")
            .approve();

    when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(approved));

    assertThatThrownBy(() -> leaveService.rejectRequest(REQUEST_ID, APPROVER_ID, "Late"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Only pending");
  }

  @Test
  void shouldCancelLeaveRequest() {
    LeaveRequest pending = LeaveRequest.create(USER_ID, LEAVE_TYPE_ID,
            LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
            BigDecimal.valueOf(5), "Vacation");

    when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));
    when(requestRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(auditLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    LeaveRequest result = leaveService.cancelRequest(REQUEST_ID, USER_ID);

    assertThat(result.getStatus()).isEqualTo(LeaveRequestStatus.CANCELLED);
  }

  @Test
  void shouldThrowWhenCancellingOthersRequest() {
    LeaveRequest pending = LeaveRequest.create(USER_ID, LEAVE_TYPE_ID,
            LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
            BigDecimal.valueOf(5), "Vacation");
    UUID otherUserId = UUID.randomUUID();

    when(requestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

    assertThatThrownBy(() -> leaveService.cancelRequest(REQUEST_ID, otherUserId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("only cancel your own");
  }
}