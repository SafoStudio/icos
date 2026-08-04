package com.safo.icos.modules.timeoff.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LeaveBalanceTest {

  @Test
  void shouldDeductDays() {
    LeaveBalance balance = LeaveBalance.builder()
            .id(UUID.randomUUID()).userId(UUID.randomUUID()).leaveTypeId(UUID.randomUUID())
            .totalDays(BigDecimal.valueOf(20)).usedDays(BigDecimal.valueOf(5))
            .remainingDays(BigDecimal.valueOf(15)).build();

    LeaveBalance updated = balance.deduct(BigDecimal.valueOf(3));

    assertThat(updated.getUsedDays()).isEqualByComparingTo("8");
    assertThat(updated.getRemainingDays()).isEqualByComparingTo("12");
    assertThat(balance.getRemainingDays()).isEqualByComparingTo("15");
  }

  @Test
  void shouldRestoreDays() {
    LeaveBalance balance = LeaveBalance.builder()
            .id(UUID.randomUUID()).userId(UUID.randomUUID()).leaveTypeId(UUID.randomUUID())
            .totalDays(BigDecimal.valueOf(20)).usedDays(BigDecimal.valueOf(8))
            .remainingDays(BigDecimal.valueOf(12)).build();

    LeaveBalance updated = balance.restore(BigDecimal.valueOf(3));

    assertThat(updated.getUsedDays()).isEqualByComparingTo("5");
    assertThat(updated.getRemainingDays()).isEqualByComparingTo("15");
  }
}