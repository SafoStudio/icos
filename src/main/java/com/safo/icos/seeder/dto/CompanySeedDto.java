package com.safo.icos.seeder.dto;

import java.util.UUID;
import lombok.Data;

@Data
public class CompanySeedDto {
  private UUID id;
  private String name;
  private String taxId;
  private Integer policyVersion;
  private String createdAt;
  private String updatedAt;
}
