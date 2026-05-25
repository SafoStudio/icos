package com.safo.icos.modules.performance.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performance_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReviewJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = "cycle_id")
    private UUID cycleId;
    
    @Column(name = "employee_id")
    private UUID employeeId;
    
    @Column(name = "manager_id")
    private UUID managerId;
    
    private Integer rating;
    private String summary;
    private String status;
    private LocalDateTime finalizedAt;
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
