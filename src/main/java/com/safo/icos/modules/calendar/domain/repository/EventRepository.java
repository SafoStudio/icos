package com.safo.icos.modules.calendar.domain.repository;

import com.safo.icos.modules.calendar.domain.model.Event;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventRepository {
    List<Event> findByUserIdAndDateRange(UUID userId, OffsetDateTime start, OffsetDateTime end);
    Event save(Event event);
}
