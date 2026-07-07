package com.safo.icos.seeder.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class PositionSeedDto {
  private UUID id;
  private UUID companyId;
  private UUID departmentId;
  private String name;
  private String description;
  private String createdAt;
  private String updatedAt;
}
