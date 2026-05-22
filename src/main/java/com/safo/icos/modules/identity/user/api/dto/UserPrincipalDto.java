package com.safo.icos.modules.user.api.dto;

import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPrincipalDto {
  private final UUID userId;
  private final String email;
  private final UUID membershipId;
  private final UUID companyId;
  private final String roleName;
  private final UUID departmentId;
  private final String departmentName;
  private final Set<String> permissions;
}
