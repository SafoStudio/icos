package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.Application;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KanbanColumnDto {
    private final VacancyStage stage;
    private final List<Application> applications;
}
