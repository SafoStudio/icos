package com.safo.icos.modules.talent.onboarding.api.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing an employee's onboarding process.
 */
@Getter
@Builder
public class OnboardingProcessDto {
  private final UUID id;
  private final UUID membershipId;
  private final UUID templateId;
  private final String status;
  private final LocalDate startDate;
  private final LocalDate targetCompletionDate;
}