package com.safo.icos.modules.identity.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Request payload for user login.
 */
@Getter
@Builder
public class LoginRequestDto {
  private final String email;
  private final String password;
}