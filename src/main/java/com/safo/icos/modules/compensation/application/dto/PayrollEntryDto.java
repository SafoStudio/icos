package com.safo.icos.modules.compensation.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

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
