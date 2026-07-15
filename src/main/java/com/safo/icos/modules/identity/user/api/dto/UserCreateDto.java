package com.safo.icos.modules.identity.user.api.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for creating a new user.
 */
@Getter
@Builder
public class UserCreateDto {
  private final String email;
  private final String firstName;
  private final String lastName;
  /** ID of the user who initiated this creation, or {@code null} for self-registration. */
  private final UUID createdBy;
}
