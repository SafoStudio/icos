package com.safo.icos.modules.performance.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceReviewTest {

  @Test
  void shouldCreatePendingReview() {
    UUID cycleId = UUID.randomUUID();
    UUID employeeId = UUID.randomUUID();
    UUID managerId = UUID.randomUUID();

    PerformanceReview review = PerformanceReview.create(cycleId, employeeId, managerId);

    assertThat(review.getId()).isNotNull();
    assertThat(review.getCycleId()).isEqualTo(cycleId);
    assertThat(review.getEmployeeId()).isEqualTo(employeeId);
    assertThat(review.getManagerId()).isEqualTo(managerId);
    assertThat(review.getStatus()).isEqualTo("PENDING");
    assertThat(review.getRating()).isNull();
    assertThat(review.getFinalizedAt()).isNull();
  }

  @Test
  void shouldFinalizeReview() {
    PerformanceReview review = PerformanceReview.create(
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    PerformanceReview finalized = review.finalize(4, "Good performance");

    assertThat(finalized.getStatus()).isEqualTo("FINALIZED");
    assertThat(finalized.getRating()).isEqualTo(4);
    assertThat(finalized.getSummary()).isEqualTo("Good performance");
    assertThat(finalized.getFinalizedAt()).isNotNull();
    assertThat(review.getStatus()).isEqualTo("PENDING");
  }
}