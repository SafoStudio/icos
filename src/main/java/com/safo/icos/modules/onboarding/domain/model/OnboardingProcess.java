package com.safo.icos.modules.onboarding.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingProcess {
    private final UUID id;
    private final UUID membershipId;
    private final UUID templateId;
    private final OnboardingProcessStatus status;
    private final LocalDate startDate;
    private final LocalDate targetCompletionDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static OnboardingProcess create(UUID membershipId, UUID templateId, LocalDate startDate) {
        return OnboardingProcess.builder()
                .id(UUID.randomUUID())
                .membershipId(membershipId)
                .templateId(templateId)
                .status(OnboardingProcessStatus.NOT_STARTED)
                .startDate(startDate)
                .targetCompletionDate(startDate.plusWeeks(2)) // Default 2 weeks
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
