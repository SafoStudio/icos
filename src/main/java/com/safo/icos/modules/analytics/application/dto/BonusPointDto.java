package com.safo.icos.modules.analytics.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BonusPointDto {
  private String period;
  private double totalBonus;
  private double totalBase;
}
