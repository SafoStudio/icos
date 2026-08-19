package com.safo.icos.modules.identity.user.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class UserProfileDto {
  private final String avatarPath;
  private final String phoneNumber;
  private final String address;
  private final LocalDate birthDate;
  private final LocalDate hireDate;
}