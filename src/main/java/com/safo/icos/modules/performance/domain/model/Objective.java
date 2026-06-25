package com.safo.icos.modules.performance.domain.model;

import com.safo.icos.modules.performance.domain.model.enums.ObjectiveStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Objective {
    private final UUID id;
    private final UUID userId;
    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal progress;
    private final ObjectiveStatus status;

    public static Objective create(UUID userId, String title, String description, LocalDate start, LocalDate end) {
        return Objective.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title(title)
                .description(description)
                .startDate(start)
                .endDate(end)
                .progress(BigDecimal.ZERO)
                .status(ObjectiveStatus.ACTIVE)
                .build();
    }
}
