package com.safo.icos.modules.analytics.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExecutiveAnalyticsDto {
  private int totalEmployees;
  private int activeProjects;
  private double turnoverRate;
  private double monthlyPayroll;
  private double overallOkrProgress;
}
