package com.safo.icos.modules.invite.infrastructure.repository;

import com.safo.icos.common.enums.RoleName;
import com.safo.icos.common.enums.InviteStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName role;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "department_id", nullable = false)
    private UUID departmentId;

    @Column(name = "manager_id")
    private UUID managerId;

    @Column(name = "invited_by_id", nullable = false)
    private UUID invitedById;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
