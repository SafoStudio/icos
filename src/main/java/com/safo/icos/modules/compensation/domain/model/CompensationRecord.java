package com.safo.icos.modules.compensation.domain.model;

import com.safo.icos.modules.compensation.domain.model.enums.PayFrequency;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class CompensationRecord {
    private final UUID id;
    private final UUID userId;
    private final BigDecimal baseSalary;
    private final String currency;
    private final PayFrequency payFrequency;
    private final LocalDate effectiveDate;

    public static CompensationRecord create(UUID userId, BigDecimal baseSalary, String currency, PayFrequency frequency, LocalDate effectiveDate) {
        return CompensationRecord.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .baseSalary(baseSalary)
                .currency(currency)
                .payFrequency(frequency)
                .effectiveDate(effectiveDate)
                .build();
    }
}
