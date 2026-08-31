package com.safo.icos.modules.talent.offboarding.api.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingProcessDto {
  private final UUID id;
  private final UUID membershipId;
  private final String status;
  private final LocalDate startDate;
  private final LocalDate lastWorkingDay;
  private final String reason;
}