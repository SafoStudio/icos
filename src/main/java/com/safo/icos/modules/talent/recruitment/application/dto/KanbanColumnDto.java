package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.Application;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a single column (stage) on the Kanban board
 * with all applications currently in that stage.
 */
@Getter
@Builder
public class KanbanColumnDto {
  private final VacancyStageDto stage;
  private final List<ApplicationDto> applications;
}
