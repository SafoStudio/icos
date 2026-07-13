package com.safo.icos.modules.talent.recruitment.domain.model;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a candidate's application to a vacancy.
 * Tracks the current pipeline stage and status.
 */
@Getter
@Builder
public class Application {
  private final UUID id;
  private final UUID candidateId;
  private final UUID vacancyId;
  /** Current stage in the recruitment pipeline. */
  private final UUID currentStageId;
  private final ApplicationStatus status;
  /** Reason for rejection, if status is {@link ApplicationStatus#REJECTED}. */
  private final String rejectionReason;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Creates a new application in {@link ApplicationStatus#ACTIVE} status
   * at the given initial stage.
   */
  public static Application create(UUID candidateId, UUID vacancyId, UUID initialStageId) {
    return Application.builder()
            .id(UUID.randomUUID())
            .candidateId(candidateId)
            .vacancyId(vacancyId)
            .currentStageId(initialStageId)
            .status(ApplicationStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}