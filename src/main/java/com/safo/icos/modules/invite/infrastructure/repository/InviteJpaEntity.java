package com.safo.icos.modules.invite.infrastructure.repository;

import com.safo.icos.modules.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.org.domain.model.enums.RoleName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
