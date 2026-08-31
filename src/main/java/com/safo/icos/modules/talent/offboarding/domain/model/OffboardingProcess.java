package com.safo.icos.modules.talent.offboarding.domain.model;

import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingProcessStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OffboardingProcess {
  private final UUID id;
  private final UUID membershipId;
  private final UUID templateId;
  private final OffboardingProcessStatus status;
  private final LocalDate startDate;
  private final LocalDate lastWorkingDay;
  private final String reason;
  private final String initiatedBy;   // MEMBER, HEAD, HR
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static OffboardingProcess create(
          UUID membershipId,
          UUID templateId,
          LocalDate lastWorkingDay,
          String reason,
          String initiatedBy
  ) {
    return OffboardingProcess.builder()
            .id(UUID.randomUUID())
            .membershipId(membershipId)
            .templateId(templateId)
            .status(OffboardingProcessStatus.PENDING_APPROVAL)
            .startDate(LocalDate.now())
            .lastWorkingDay(lastWorkingDay)
            .reason(reason)
            .initiatedBy(initiatedBy)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}