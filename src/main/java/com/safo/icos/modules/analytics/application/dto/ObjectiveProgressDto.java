package com.safo.icos.modules.analytics.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObjectiveProgressDto {
  private UUID id;
  private String title;
  private double progress;
  private List<KeyResultProgressDto> keyResults;
}
