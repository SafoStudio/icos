package com.safo.icos.modules.talent.onboarding.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing an onboarding template.
 * Templates belong to a company and contain a list of task templates.
 */
@Getter
@Builder
public class OnboardingTemplate {
  private final UUID id;
  private final UUID companyId;
  private final String name;
  private final String description;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OnboardingTemplate create(UUID companyId, String name, String description) {
    return OnboardingTemplate.builder()
            .id(UUID.randomUUID())
            .companyId(companyId)
            .name(name)
            .description(description)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}