package com.safo.icos.modules.identity.user.api.dto;

import java.util.Set;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO containing full security context data for a user in a specific company.
 * Used by the security layer to construct
 * {@link com.safo.icos.security.context.IcosUserPrincipal}.
 */
@Getter
@Builder
public class UserPrincipalDto {
  private final UUID userId;
  private final String email;
  private final UUID membershipId;
  /** Tenant ID — the company the user is currently acting within. */
  private final UUID companyId;
  private final String roleName;
  private final UUID departmentId;
  private final String departmentName;
  /** Permission keys granted to the user in this company. */
  private final Set<String> permissions;
}