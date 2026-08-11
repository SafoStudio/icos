package com.safo.icos.modules.compensation.application.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayrollEntryDto {
  private final UUID userId;
  private final String employeeName;
  private final BigDecimal baseAmount;
  private final BigDecimal bonusAmount;
  private final BigDecimal totalAmount;
  private final String currency;
}