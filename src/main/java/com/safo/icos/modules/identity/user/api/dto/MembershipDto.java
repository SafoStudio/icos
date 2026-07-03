package com.safo.icos.modules.identity.user.api.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a user's membership in a company.
 */
@Getter
@Builder
public class MembershipDto {
  private final UUID id;
  private final UUID userId;
  private final UUID companyId;
  /** Role name (e.g., OWNER, ADMIN, HEAD, EMPLOYEE). */
  private final String role;
  private final UUID roleId;
  private final UUID departmentId;
  private final boolean active;
}