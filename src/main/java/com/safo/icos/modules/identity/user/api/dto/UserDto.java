package com.safo.icos.modules.identity.user.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a user with their current company membership details.
 * Used as a return type by {@link com.safo.icos.modules.identity.user.api.UserProvider}.
 */
@Getter
@Builder
public class UserDto {
  private final UUID id;
  private final String email;
  private final String firstName;
  private final String lastName;
  /** Company ID as string for the user's current membership. */
  private final String companyId;
  /** Role name of the user in the current company. */
  private final String role;
  private final UUID departmentId;
  /** Permission keys granted to the user. */
  private final List<String> permissions;
  private final String status;
  private final UserProfileDto profile;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}
