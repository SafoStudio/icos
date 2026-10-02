package com.safo.icos.modules.analytics.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalaryBucketDto {
  private String label;
  private UUID departmentId;
  private String departmentName;
  private double min;
  private double max;
  private double avg;
  private int count;
}
