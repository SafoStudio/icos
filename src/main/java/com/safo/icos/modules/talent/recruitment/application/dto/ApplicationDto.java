package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a candidate's application to a vacancy.
 */
@Getter
@Builder
public class ApplicationDto {
  private final UUID id;
  private final UUID candidateId;
  private final UUID vacancyId;
  /** Current pipeline stage. */
  private final UUID currentStageId;
  private final ApplicationStatus status;
  private final LocalDateTime createdAt;
}