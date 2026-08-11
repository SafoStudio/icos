package com.safo.icos.modules.compensation.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BonusTest {

  @Test
  void shouldCreateBonus() {
    UUID userId = UUID.randomUUID();
    LocalDate paymentDate = LocalDate.of(2026, 12, 15);

    Bonus bonus = Bonus.create(userId, BigDecimal.valueOf(1000), "Performance", "Q3 bonus", paymentDate);

    assertThat(bonus.getId()).isNotNull();
    assertThat(bonus.getUserId()).isEqualTo(userId);
    assertThat(bonus.getAmount()).isEqualByComparingTo("1000");
    assertThat(bonus.getType()).isEqualTo("Performance");
    assertThat(bonus.getDescription()).isEqualTo("Q3 bonus");
    assertThat(bonus.getStatus()).isEqualTo("PENDING");
    assertThat(bonus.getPaymentDate()).isEqualTo(paymentDate);
  }
}