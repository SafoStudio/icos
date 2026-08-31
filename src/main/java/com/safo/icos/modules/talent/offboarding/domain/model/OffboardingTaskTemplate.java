package com.safo.icos.modules.talent.offboarding.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingTaskTemplate {
  private final UUID id;
  private final UUID templateId;
  private final String title;
  private final String description;
  private final Integer daysFromStart;
  private final String assigneeRole;    // HR, HEAD, MEMBER
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OffboardingTaskTemplate create(
          UUID templateId,
          String title,
          String description,
          Integer daysFromStart,
          String assigneeRole
  ) {
    return OffboardingTaskTemplate.builder()
            .id(UUID.randomUUID())
            .templateId(templateId)
            .title(title)
            .description(description)
            .daysFromStart(daysFromStart)
            .assigneeRole(assigneeRole)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}