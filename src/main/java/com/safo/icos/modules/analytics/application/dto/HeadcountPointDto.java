package com.safo.icos.modules.analytics.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HeadcountPointDto {
  private String period;
  private int headcount;
  private int hired;
  private int fired;
  private double turnoverRate;
}
