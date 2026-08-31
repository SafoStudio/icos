package com.safo.icos.modules.talent.offboarding.domain.model;

import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingTaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingTask {
  private final UUID id;
  private final UUID processId;
  private final String title;
  private final String description;
  private final UUID assigneeId;
  private final OffboardingTaskStatus status;
  private final LocalDate dueDate;
  private final LocalDateTime completedAt;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OffboardingTask create(
          UUID processId,
          String title,
          String description,
          UUID assigneeId,
          LocalDate dueDate
  ) {
    return OffboardingTask.builder()
            .id(UUID.randomUUID())
            .processId(processId)
            .title(title)
            .description(description)
            .assigneeId(assigneeId)
            .status(OffboardingTaskStatus.PENDING)
            .dueDate(dueDate)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}