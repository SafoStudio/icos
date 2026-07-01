package com.safo.icos.modules.identity.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * Response returned after a successful token refresh.
 * Contains a new access token and a new rotated refresh token.
 */
@Getter
@Builder
public class RefreshResponseDto {
  private final String accessToken;
  private final String refreshToken;
  /** Token lifetime in seconds. */
  private final int expiresIn;
}