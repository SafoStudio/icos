package com.safo.icos.modules.auth.presentation.dto;

import com.safo.icos.common.dto.UserDto;
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