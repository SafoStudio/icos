package com.safo.icos.modules.identity.auth.presentation.dto;

import com.safo.icos.modules.identity.user.api.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseView {
  private final String accessToken;
  private final String tokenType;
  private final int expiresIn;
  private final UserDto user;
}
