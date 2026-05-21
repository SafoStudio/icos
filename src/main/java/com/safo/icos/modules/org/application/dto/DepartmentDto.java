package com.safo.icos.modules.org.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentDto {
  private final UUID id;
  private final UUID companyId;
  private final String name;
  private final String description;
}
