package com.safo.icos.modules.talent.recruitment.application.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KanbanBoardDto {
    private final UUID vacancyId;
    private final List<KanbanColumnDto> columns;
}
