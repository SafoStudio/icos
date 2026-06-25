package com.safo.icos.modules.identity.user.infrastructure.repository.entity;

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

@Entity
@Table(name = "memberships")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipJpaEntity {
  @Id
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "company_id", nullable = false)
  private UUID companyId;

  @Column(name = "role_id", nullable = false)
  private UUID roleId;

  @Column(name = "department_id")
  private UUID departmentId;

  @Column(name = "manager_id")
  private UUID managerId;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
