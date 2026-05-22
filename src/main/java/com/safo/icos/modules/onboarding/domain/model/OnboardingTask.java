package com.safo.icos.modules.onboarding.domain.model;

import com.safo.icos.modules.onboarding.domain.model.enums.OnboardingTaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingTask {
    private final UUID id;
    private final UUID processId;
    private final String title;
    private final String description;
    private final UUID assigneeId;
    private final OnboardingTaskStatus status;
    private final LocalDate dueDate;
    private final LocalDateTime completedAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static OnboardingTask create(UUID processId, String title, String description, UUID assigneeId, LocalDate dueDate) {
        return OnboardingTask.builder()
                .id(UUID.randomUUID())
                .processId(processId)
                .title(title)
                .description(description)
                .assigneeId(assigneeId)
                .status(OnboardingTaskStatus.PENDING)
                .dueDate(dueDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
