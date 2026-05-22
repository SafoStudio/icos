package com.safo.icos.modules.user.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateDto {
  private final String email;
  private final String firstName;
  private final String lastName;
  private final UUID createdBy;
}
