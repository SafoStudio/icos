package com.safo.icos.modules.org.infrastructure.repository.entity;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "department_permissions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentPermissionJpaEntity {

  @Id
  private UUID id;

  @Column(name = "department_id", nullable = false)
  private UUID departmentId;

  @Column(name = "role_name", nullable = false)
  private String roleName;

  @Column(name = "permission_id", nullable = false)
  private UUID permissionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "permission_id", insertable = false, updatable = false)
  private PermissionJpaEntity permission;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
