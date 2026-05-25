package com.safo.icos.modules.talent.recruitment.application.dto;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApplicationDto {
    private UUID id;
    private UUID candidateId;
    private UUID vacancyId;
    private UUID currentStageId;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
}
