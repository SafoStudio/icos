package com.safo.icos.modules.compensation.domain.model;

import com.safo.icos.modules.compensation.domain.model.enums.PayFrequency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CompensationRecordTest {

  @Test
  void shouldCreateCompensationRecord() {
    UUID userId = UUID.randomUUID();
    LocalDate effectiveDate = LocalDate.of(2026, 8, 1);

    CompensationRecord record = CompensationRecord.create(
            userId, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, effectiveDate);

    assertThat(record.getId()).isNotNull();
    assertThat(record.getUserId()).isEqualTo(userId);
    assertThat(record.getBaseSalary()).isEqualByComparingTo("5000");
    assertThat(record.getCurrency()).isEqualTo("USD");
    assertThat(record.getPayFrequency()).isEqualTo(PayFrequency.MONTHLY);
    assertThat(record.getEffectiveDate()).isEqualTo(effectiveDate);
  }
}