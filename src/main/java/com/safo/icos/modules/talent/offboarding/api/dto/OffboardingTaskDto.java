package com.safo.icos.modules.talent.offboarding.api.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object representing a task in an offboarding process.
 */
@Getter
@Builder
public class OffboardingTaskDto {
  private final UUID id;
  private final UUID processId;
  private final String title;
  private final String description;
  private final String status;
  private final LocalDate dueDate;
  private final LocalDateTime completedAt;
  private final UUID assigneeId;
}