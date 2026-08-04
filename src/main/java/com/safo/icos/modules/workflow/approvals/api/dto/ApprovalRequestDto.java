package com.safo.icos.modules.workflow.approvals.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing an approval request.
 */
@Getter
@Builder
public class ApprovalRequestDto {
  private final UUID id;
  private final String targetEntityId;
  private final String targetEntityType;
  private final UUID requesterId;
  private final UUID approverId;
  private final String status;
}