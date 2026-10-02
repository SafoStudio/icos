package com.safo.icos.modules.analytics.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeOffDayDto {
  private String date;
  private UUID departmentId;
  private String departmentName;
  private int absentCount;
  private int totalCount;
  private double loadPercent;
}
