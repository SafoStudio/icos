package com.safo.icos.modules.org.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a department permission assignment.
 * <p>
 * Stores which permissions are granted to a specific role (HEAD or SPECIALIST)
 * within a specific department.
 */
@Getter
@Builder
public class DepartmentPermission {

  private final UUID id;
  private final UUID departmentId;
  private final String roleName;
  private final UUID permissionId;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Creates a new DepartmentPermission.
   *
   * @param departmentId the department UUID
   * @param roleName     the role name (HEAD or SPECIALIST)
   * @param permissionId the permission UUID
   * @return a new DepartmentPermission instance
   */
  public static DepartmentPermission create(UUID departmentId, String roleName, UUID permissionId) {
    return DepartmentPermission.builder()
            .id(UUID.randomUUID())
            .departmentId(departmentId)
            .roleName(roleName)
            .permissionId(permissionId)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}
