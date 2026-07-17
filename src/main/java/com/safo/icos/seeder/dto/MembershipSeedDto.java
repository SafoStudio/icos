package com.safo.icos.seeder.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class MembershipSeedDto {
  private UUID id;
  private UUID userId;
  private UUID companyId;
  private String roleName;
  private UUID departmentId;
  private UUID positionId;
  private UUID managerId;
  private boolean active;
  private String createdAt;
  private String updatedAt;
}
