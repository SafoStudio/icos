package com.safo.icos.modules.timeoff.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leave_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveAuditLogJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = "leave_request_id")
    private UUID leaveRequestId;
    
    @Column(name = "actor_id")
    private UUID actorId;
    
    private String eventType;
    private String metadata;
    private LocalDateTime createdAt;
}
