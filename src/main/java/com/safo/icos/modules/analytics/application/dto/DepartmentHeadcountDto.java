package com.safo.icos.modules.analytics.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentHeadcountDto {
  private UUID departmentId;
  private String departmentName;
  private List<HeadcountPointDto> points;
}
