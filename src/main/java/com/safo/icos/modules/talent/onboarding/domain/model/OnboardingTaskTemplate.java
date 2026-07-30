package com.safo.icos.modules.talent.onboarding.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a task definition within an onboarding template.
 * The {@code daysFromStart} field determines when the task is due
 * relative to the onboarding start date.
 */
@Getter
@Builder
public class OnboardingTaskTemplate {
  private final UUID id;
  private final UUID templateId;
  private final String title;
  private final String description;
  /** Number of days after onboarding start when this task is due. */
  private final Integer daysFromStart;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OnboardingTaskTemplate create(UUID templateId, String title,
                                              String description, Integer daysFromStart) {
    return OnboardingTaskTemplate.builder()
            .id(UUID.randomUUID())
            .templateId(templateId)
            .title(title)
            .description(description)
            .daysFromStart(daysFromStart)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}