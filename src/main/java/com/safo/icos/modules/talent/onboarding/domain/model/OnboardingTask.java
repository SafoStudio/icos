package com.safo.icos.modules.talent.onboarding.domain.model;

import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingTaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a single task in an onboarding process.
 * Created in {@link OnboardingTaskStatus#PENDING} status.
 */
@Getter
@Builder
public class OnboardingTask {
  private final UUID id;
  private final UUID processId;
  private final String title;
  private final String description;
  /** User assigned to complete this task. */
  private final UUID assigneeId;
  private final OnboardingTaskStatus status;
  private final LocalDate dueDate;
  /** Timestamp when the task was marked as DONE. */
  private final LocalDateTime completedAt;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OnboardingTask create(UUID processId, String title, String description,
                                      UUID assigneeId, LocalDate dueDate) {
    return OnboardingTask.builder()
            .id(UUID.randomUUID())
            .processId(processId)
            .title(title)
            .description(description)
            .assigneeId(assigneeId)
            .status(OnboardingTaskStatus.PENDING)
            .dueDate(dueDate)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}