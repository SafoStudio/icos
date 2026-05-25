package com.safo.icos.modules.compensation.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SalaryRevision {
    private final UUID id;
    private final UUID userId;
    private final BigDecimal previousSalary;
    private final BigDecimal newSalary;
    private final String reason;
    private final LocalDate effectiveDate;
    private final UUID approvedBy;
    private final LocalDateTime createdAt;

    public static SalaryRevision create(UUID userId, BigDecimal oldSalary, BigDecimal newSalary, String reason, LocalDate effectiveDate, UUID approvedBy) {
        return SalaryRevision.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .previousSalary(oldSalary)
                .newSalary(newSalary)
                .reason(reason)
                .effectiveDate(effectiveDate)
                .approvedBy(approvedBy)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
