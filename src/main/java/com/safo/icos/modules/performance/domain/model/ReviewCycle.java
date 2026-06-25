package com.safo.icos.modules.performance.domain.model;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class ReviewCycle {
    private final UUID id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status; // ACTIVE, CLOSED

    public static ReviewCycle create(String name, LocalDate start, LocalDate end) {
        return ReviewCycle.builder()
                .id(UUID.randomUUID())
                .name(name)
                .startDate(start)
                .endDate(end)
                .status("ACTIVE")
                .build();
    }
}
