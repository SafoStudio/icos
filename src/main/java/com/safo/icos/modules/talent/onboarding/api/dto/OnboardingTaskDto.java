package com.safo.icos.modules.talent.onboarding.api.dto;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a single task within an onboarding process.
 */
@Getter
@Builder
public class OnboardingTaskDto {
  private final UUID id;
  private final String title;
  private final String description;
  private final String status;
  private final LocalDate dueDate;
}