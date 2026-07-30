package com.safo.icos.modules.talent.onboarding.domain.model;

import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingProcessStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing an employee's onboarding process.
 * Created from a template with a default 2-week target completion.
 */
@Getter
@Builder
public class OnboardingProcess {
  private final UUID id;
  private final UUID membershipId;
  private final UUID templateId;
  private final OnboardingProcessStatus status;
  private final LocalDate startDate;
  /** Default target is 2 weeks from start date. */
  private final LocalDate targetCompletionDate;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OnboardingProcess create(UUID membershipId, UUID templateId, LocalDate startDate) {
    return OnboardingProcess.builder()
            .id(UUID.randomUUID())
            .membershipId(membershipId)
            .templateId(templateId)
            .status(OnboardingProcessStatus.NOT_STARTED)
            .startDate(startDate)
            .targetCompletionDate(startDate.plusWeeks(2))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}