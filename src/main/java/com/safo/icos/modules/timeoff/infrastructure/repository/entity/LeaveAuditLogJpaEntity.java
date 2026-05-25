package com.safo.icos.modules.timeoff.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

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
