package com.safo.icos.modules.workflow.approvals.infrastructure.repository.entity;

import com.safo.icos.modules.workflow.approvals.domain.model.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "approval_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestJpaEntity {
    @Id
    private UUID id;
    private String targetEntityId;
    private String targetEntityType;
    private UUID requesterId;
    private UUID approverId;
    
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
