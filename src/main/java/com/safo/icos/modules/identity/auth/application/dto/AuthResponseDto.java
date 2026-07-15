package com.safo.icos.modules.identity.auth.application.dto;

import com.safo.icos.modules.identity.user.api.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

/**
 * Response returned after successful authentication (login, register, invite acceptance).
 * Contains the access token (JWT), refresh token (opaque, also set as HTTP-only cookie),
 * and the authenticated user's profile with company membership.
 */
@Getter
@Builder
public class AuthResponseDto {
  private final String accessToken;
  private final String refreshToken;
  private final String tokenType;
  /** Token lifetime in seconds. */
  private final int expiresIn;
  private final UserDto user;
}
