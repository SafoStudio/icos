package com.safo.icos.modules.performance.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PerformanceReview {
  private final UUID id;
  private final UUID cycleId;
  private final UUID employeeId;
  private final UUID managerId;
  private final Integer rating;
  private final String summary;
  private final String status;
  private final LocalDateTime finalizedAt;

  public static PerformanceReview create(UUID cycleId, UUID employeeId, UUID managerId) {
    return PerformanceReview.builder()
            .id(UUID.randomUUID()).cycleId(cycleId)
            .employeeId(employeeId).managerId(managerId).status("PENDING").build();
  }

  public PerformanceReview finalize(Integer rating, String summary) {
    return this.toBuilder().rating(rating).summary(summary)
            .status("FINALIZED").finalizedAt(LocalDateTime.now()).build();
  }
}
