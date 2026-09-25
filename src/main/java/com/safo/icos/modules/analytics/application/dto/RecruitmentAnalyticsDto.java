package com.safo.icos.modules.analytics.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentAnalyticsDto {
  private int openVacancies;
  private int totalCandidates;
  private int avgTimeToHireDays;
  private double offerAcceptanceRate;
}
