package com.safo.icos.modules.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequestDto {
  private final String email;
  private final String password;
}