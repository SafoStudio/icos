package com.safo.icos.modules.workflow.approvals.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.workflow.approvals.api.event.ApprovalApprovedEvent;
import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import com.safo.icos.modules.workflow.approvals.domain.model.enums.ApprovalStatus;
import com.safo.icos.modules.workflow.approvals.domain.repository.ApprovalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovalServiceTest {

  @Mock private ApprovalRepository approvalRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private ApprovalService approvalService;

  private static final UUID APPROVER_ID = UUID.randomUUID();
  private static final UUID REQUEST_ID = UUID.randomUUID();

  @Test
  void shouldApproveRequest() {
    ApprovalRequest pending = pendingRequest();
    when(approvalRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));
    when(approvalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    ApprovalRequest result = approvalService.approveRequest(REQUEST_ID, APPROVER_ID);

    assertThat(result.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
    verify(eventPublisher).publishEvent(any(ApprovalApprovedEvent.class));
  }

  @Test
  void shouldThrowWhenApprovingNonExistentRequest() {
    when(approvalRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> approvalService.approveRequest(REQUEST_ID, APPROVER_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldThrowWhenApprovingAsWrongApprover() {
    ApprovalRequest pending = ApprovalRequest.builder()
            .id(REQUEST_ID).targetEntityId("leave-1").targetEntityType("LEAVE_REQUEST")
            .requesterId(UUID.randomUUID()).approverId(UUID.randomUUID())
            .status(ApprovalStatus.PENDING).build();
    UUID wrongApprover = UUID.randomUUID();

    when(approvalRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

    assertThatThrownBy(() -> approvalService.approveRequest(REQUEST_ID, wrongApprover))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not authorized");
  }

  @Test
  void shouldRejectRequest() {
    ApprovalRequest pending = pendingRequest();
    when(approvalRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));
    when(approvalRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    ApprovalRequest result = approvalService.rejectRequest(REQUEST_ID, APPROVER_ID);

    assertThat(result.getStatus()).isEqualTo(ApprovalStatus.REJECTED);
  }

  @Test
  void shouldGetApprovalsForUser() {
    ApprovalRequest req1 = pendingRequest();
    ApprovalRequest req2 = pendingRequest();
    when(approvalRepository.findAllByApproverId(APPROVER_ID)).thenReturn(List.of(req1, req2));

    List<ApprovalRequest> result = approvalService.getApprovalsForUser(APPROVER_ID);

    assertThat(result).hasSize(2);
  }

  private ApprovalRequest pendingRequest() {
    return ApprovalRequest.builder()
            .id(REQUEST_ID)
            .targetEntityId("leave-123")
            .targetEntityType("LEAVE_REQUEST")
            .requesterId(UUID.randomUUID())
            .approverId(APPROVER_ID)
            .status(ApprovalStatus.PENDING)
            .build();
  }
}