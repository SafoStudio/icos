package com.safo.icos.modules.compensation.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalaryRevisionDto {
    private final UUID id;
    private final UUID userId;
    private final BigDecimal previousSalary;
    private final BigDecimal newSalary;
    private final String reason;
    private final LocalDate effectiveDate;
}
