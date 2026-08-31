package com.safo.icos.modules.talent.offboarding.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingTemplateDto {
  private final UUID id;
  private final UUID companyId;
  private final String name;
  private final String description;
}