package com.safo.icos.modules.identity.auth.api.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO returned after a successful access token generation.
 * Contains only the short-lived JWT access token and its metadata.
 * The refresh token is managed separately through domain services
 * and delivered to the client via HTTP-only cookie.
 */
@Getter
@Builder
public class AuthTokenDto {

  /** JWT access token used for authenticating API requests. */
  private final String accessToken;

  /** Token type, typically {@code "Bearer"}. */
  private final String tokenType;

  /** Token lifetime in seconds. */
  private final int expiresIn;
}
