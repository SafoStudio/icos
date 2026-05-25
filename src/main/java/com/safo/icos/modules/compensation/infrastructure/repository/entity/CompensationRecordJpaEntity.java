package com.safo.icos.modules.compensation.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
