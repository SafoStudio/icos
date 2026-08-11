package com.safo.icos.modules.compensation.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SalaryRevisionTest {

  @Test
  void shouldCreateSalaryRevision() {
    UUID userId = UUID.randomUUID();
    UUID approvedBy = UUID.randomUUID();
    LocalDate effectiveDate = LocalDate.of(2026, 9, 1);

    SalaryRevision revision = SalaryRevision.create(
            userId, BigDecimal.valueOf(5000), BigDecimal.valueOf(6000),
            "Annual raise", effectiveDate, approvedBy);

    assertThat(revision.getId()).isNotNull();
    assertThat(revision.getUserId()).isEqualTo(userId);
    assertThat(revision.getPreviousSalary()).isEqualByComparingTo("5000");
    assertThat(revision.getNewSalary()).isEqualByComparingTo("6000");
    assertThat(revision.getReason()).isEqualTo("Annual raise");
    assertThat(revision.getEffectiveDate()).isEqualTo(effectiveDate);
    assertThat(revision.getApprovedBy()).isEqualTo(approvedBy);
    assertThat(revision.getCreatedAt()).isNotNull();
  }
}