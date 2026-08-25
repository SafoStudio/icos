package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * Request payload for creating a new vacancy.
 */
@Getter
@Setter
public class CreateVacancyRequest {
  private UUID companyId;
  private UUID departmentId;
  /** Position ID from the org module. */
  private UUID positionId;
  private String title;
  private String description;
  private String requirements;
  private VacancyStatus status;
}
