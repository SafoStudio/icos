package com.safo.icos.modules.org.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for department permission assignments.
 */
@Data
@Builder
public class DepartmentPermissionDto {
  private UUID id;
  private UUID departmentId;
  private String roleName;
  private UUID permissionId;
}
