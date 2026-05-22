package com.safo.icos.modules.recruitment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Application {
    private final UUID id;
    private final UUID candidateId;
    private final UUID vacancyId;
    private final UUID currentStageId;
    private final ApplicationStatus status;
    private final String rejectionReason;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Application create(UUID candidateId, UUID vacancyId, UUID initialStageId) {
        return Application.builder()
                .id(UUID.randomUUID())
                .candidateId(candidateId)
                .vacancyId(vacancyId)
                .currentStageId(initialStageId)
                .status(ApplicationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
