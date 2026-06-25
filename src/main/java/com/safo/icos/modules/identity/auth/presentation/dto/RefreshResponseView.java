package com.safo.icos.modules.identity.auth.presentation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshResponseView {
  private final String accessToken;
  private final int expiresIn;
}
