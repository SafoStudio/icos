package com.safo.icos.modules.org.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionDto {
  private UUID id;
  private String name;
}
