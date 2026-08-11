package com.safo.icos.modules.compensation.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompensationDto {
  private final UUID id;
  private final UUID userId;
  private final BigDecimal baseSalary;
  private final String currency;
  private final String payFrequency;
  private final LocalDate effectiveDate;
}