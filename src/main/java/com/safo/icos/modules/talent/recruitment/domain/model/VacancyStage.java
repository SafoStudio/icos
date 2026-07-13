package com.safo.icos.modules.talent.recruitment.domain.model;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.PipelineStageType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a stage in a vacancy's recruitment pipeline.
 * Each vacancy has its own snapshot of stages.
 */
@Getter
@Builder
public class VacancyStage {
  private final UUID id;
  private final UUID vacancyId;
  private final String name;
  private final PipelineStageType type;
  /** Zero-based ordering for Kanban display. */
  private final Integer orderIndex;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  public static VacancyStage create(UUID vacancyId, String name,
                                    PipelineStageType type, Integer orderIndex) {
    return VacancyStage.builder()
            .id(UUID.randomUUID())
            .vacancyId(vacancyId)
            .name(name)
            .type(type)
            .orderIndex(orderIndex)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }
}