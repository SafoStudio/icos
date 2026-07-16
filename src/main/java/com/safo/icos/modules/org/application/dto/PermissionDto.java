package com.safo.icos.modules.org.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for Permission.
 * <p>
 * Represents a system permission that can be assigned to roles or departments.
 * Used for displaying permissions in UI and for permission assignment operations.
 */
@Data
@Builder
public class PermissionDto {

  /** Unique identifier of the permission. */
  private UUID id;

  /** Technical name of the permission (e.g., "department:manage", "recruitment:read"). */
  private String name;

  /** Human-readable description of the permission (e.g., "Manage departments"). */
  private String description;
}