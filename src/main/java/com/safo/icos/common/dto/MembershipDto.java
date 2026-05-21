package com.safo.icos.common.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MembershipDto {
  private final UUID id;
  private final UUID userId;
  private final UUID companyId;
  private final String role;
  private final UUID roleId;
  private final UUID departmentId;
  private final boolean active;
}