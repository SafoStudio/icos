package com.safo.icos.modules.talent.offboarding.api.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Data transfer object representing an employee offboarding process.
 */
@Getter
@Builder
public class OffboardingProcessDto {
  private final UUID id;
  private final UUID membershipId;
  private final UUID templateId;
  private final String status;
  private final LocalDate startDate;
  private final LocalDate lastWorkingDay;
  private final String reason;
  private final String initiatedBy;
}