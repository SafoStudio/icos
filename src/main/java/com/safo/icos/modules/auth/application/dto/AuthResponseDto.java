package com.safo.icos.modules.auth.application.dto;

import com.safo.icos.common.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final int expiresIn;
    private final UserDto user;
}