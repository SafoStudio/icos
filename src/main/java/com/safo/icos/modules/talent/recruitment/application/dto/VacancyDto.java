package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VacancyDto {
    private UUID id;
    private UUID companyId;
    private UUID departmentId;
    private UUID positionId;
    private String title;
    private String description;
    private String requirements;
    private VacancyStatus status;
    private LocalDateTime createdAt;
}
