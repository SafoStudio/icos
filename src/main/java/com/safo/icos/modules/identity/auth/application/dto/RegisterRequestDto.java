package com.safo.icos.modules.identity.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Request payload for new user registration.
 */
@Getter
@Builder
public class RegisterRequestDto {
  private final String email;
  private final String password;
  private final String firstName;
  private final String lastName;
  private final String companyName;
}