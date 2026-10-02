package com.safo.icos.modules.analytics.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KeyResultProgressDto {
  private UUID id;
  private String title;
  private double progress;
  private double weight;
  private String status;
}
