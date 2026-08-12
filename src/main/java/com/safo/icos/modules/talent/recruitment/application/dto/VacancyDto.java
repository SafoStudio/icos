package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a job vacancy.
 */
@Getter
@Builder
public class VacancyDto {
  private final UUID id;
  private final UUID companyId;
  private final UUID departmentId;
  /** Position ID from the org module. */
  private final UUID positionId;
  private final String title;
  private final String description;
  private final String requirements;
  private final VacancyStatus status;
  private final UUID createdBy;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}
