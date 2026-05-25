package com.safo.icos.modules.performance.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "okr_key_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyResultJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = "objective_id")
    private UUID objectiveId;
    
    private String title;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private String unit;
    private BigDecimal weight;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
