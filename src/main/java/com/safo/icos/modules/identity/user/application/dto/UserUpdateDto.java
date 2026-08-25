package com.safo.icos.modules.identity.user.application.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserUpdateDto {
  private String firstName;
  private String lastName;
  private String email;
  private String role;
  private UUID departmentId;
}
