package com.safo.icos.modules.talent.offboarding.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingTaskTemplateDto {
  private final UUID id;
  private final UUID templateId;
  private final String title;
  private final String description;
  private final Integer daysFromStart;
  private final String assigneeRole;
}