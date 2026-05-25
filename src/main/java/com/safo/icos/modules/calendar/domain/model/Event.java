package com.safo.icos.modules.calendar.domain.model;

import com.safo.icos.modules.calendar.domain.model.enums.EventType;
import com.safo.icos.modules.calendar.domain.model.enums.SourceType;
import com.safo.icos.modules.calendar.domain.model.enums.Visibility;
import lombok.Builder;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
public class Event {
    private final UUID id;
    private final UUID userId;
    private final String title;
    private final String description;
    private final EventType type;
    private final SourceType source;
    private final OffsetDateTime startAt;
    private final OffsetDateTime endAt;
    private final Visibility visibility;
}
