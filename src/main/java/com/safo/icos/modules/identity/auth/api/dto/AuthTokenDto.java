package com.safo.icos.modules.auth.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenDto {
  private final String accessToken;
  private final String refreshToken;
  private final String tokenType;
  private final int expiresIn;
}
