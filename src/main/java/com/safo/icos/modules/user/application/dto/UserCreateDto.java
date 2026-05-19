package com.safo.icos.modules.user.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class UserCreateDto {
    private final String email;
    private final String firstName;
    private final String lastName;
    private final UUID companyId;
}