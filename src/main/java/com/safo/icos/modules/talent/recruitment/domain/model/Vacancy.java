package com.safo.icos.modules.talent.recruitment.domain.model;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Domain entity representing a job vacancy.
 * Created in {@link VacancyStatus#DRAFT} status by default,
 * or with a specified status if provided.
 */
@Getter
@Builder
public class Vacancy {
  private final UUID id;
  private final UUID companyId;
  private final UUID departmentId;
  private final UUID positionId;
  private final String title;
  private final String description;
  private final String requirements;
  @Setter
  private VacancyStatus status;
  private final UUID createdBy;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}