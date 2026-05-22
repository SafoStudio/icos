package com.safo.icos.modules.recruitment.domain.model;

import com.safo.icos.modules.recruitment.domain.model.enums.VacancyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Vacancy {
    private final UUID id;
    private final UUID companyId;
    private final UUID departmentId;
    private final UUID positionId;
    private final String title;
    private final String description;
    private final String requirements;
    private final VacancyStatus status;
    private final UUID createdBy;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Vacancy create(UUID companyId, UUID departmentId, UUID positionId, String title, String description, String requirements, UUID createdBy) {
        return Vacancy.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .departmentId(departmentId)
                .positionId(positionId)
                .title(title)
                .description(description)
                .requirements(requirements)
                .status(VacancyStatus.DRAFT)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
