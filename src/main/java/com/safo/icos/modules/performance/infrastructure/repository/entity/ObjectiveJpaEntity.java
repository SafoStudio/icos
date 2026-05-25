package com.safo.icos.modules.performance.infrastructure.repository.entity;

import com.safo.icos.modules.performance.domain.model.enums.ObjectiveStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "okr_objectives")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectiveJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = "user_id")
    private UUID userId;
    
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal progress;
    
    @Enumerated(EnumType.STRING)
    private ObjectiveStatus status;
    
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
