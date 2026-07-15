package com.safo.icos.modules.talent.recruitment.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a Kanban board view of a vacancy's recruitment pipeline.
 * Contains columns grouped by pipeline stage, each with its applications.
 */
@Getter
@Builder
public class KanbanBoardDto {
  private final UUID vacancyId;
  private final List<KanbanColumnDto> columns;
}
