package com.safo.icos.modules.talent.offboarding.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingTemplate {
  private final UUID id;
  private final UUID companyId;
  private final String name;
  private final String description;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OffboardingTemplate create(UUID companyId, String name, String description) {
    return OffboardingTemplate.builder()
            .id(UUID.randomUUID())
            .companyId(companyId)
            .name(name)
            .description(description)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}