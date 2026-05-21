package com.safo.icos.modules.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshResponseDto {
  private final String accessToken;
  private final String refreshToken;
  private final int expiresIn;
}