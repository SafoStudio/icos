package com.safo.icos.modules.calendar.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class EventDto {
    private final UUID id;
    private final UUID userId;
    private final String title;
    private final String description;
    private final OffsetDateTime startAt;
    private final OffsetDateTime endAt;
}
