package com.safo.icos.modules.auth.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterRequestDto {
    private final String email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String companyName;
}