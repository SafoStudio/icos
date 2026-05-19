package com.safo.icos.modules.user.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateDto {
    private final String firstName;
    private final String lastName;
    private final String email;
}