package com.safo.icos.seeder.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class DepartmentSeedDto {
  private UUID id;
  private UUID companyId;
  private UUID parentId;
  private String name;
  private String description;
  private String createdAt;
  private String updatedAt;
}
