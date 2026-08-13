package com.safo.icos.modules.calendar.domain.repository;

import com.safo.icos.modules.calendar.domain.model.Event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Event} persistence.
 */
public interface EventRepository {
  List<Event> findByUserIdAndDateRange(UUID userId, OffsetDateTime start, OffsetDateTime end);
  Optional<Event> findById(UUID id);
  Event save(Event event);
  void deleteById(UUID id);
}