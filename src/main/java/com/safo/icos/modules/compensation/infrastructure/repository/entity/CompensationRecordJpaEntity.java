package com.safo.icos.modules.compensation.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compensation_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompensationRecordJpaEntity {
    @Id
    private UUID id;

    @Column(name = "user_id", unique = true)
    private UUID userId;

    private BigDecimal baseSalary;
    private String currency;
    private String payFrequency;
    private LocalDate effectiveDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
