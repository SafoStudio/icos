package com.safo.icos.modules.talent.recruitment.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

/**
 * DTO representing a stage in a vacancy recruitment pipeline.
 */
@Getter
@Builder
public class VacancyStageDto {
  private final UUID id;
  private final String name;
  private final String type;
  private final Integer orderIndex;
}