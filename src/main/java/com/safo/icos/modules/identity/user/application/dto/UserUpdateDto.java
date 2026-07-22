package com.safo.icos.modules.identity.user.application.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
  private String firstName;
  private String lastName;
  private String email;
  private String role;
  private UUID departmentId;
}
