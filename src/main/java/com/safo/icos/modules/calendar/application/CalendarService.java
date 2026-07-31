package com.safo.icos.modules.calendar.application;

import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for calendar event management.
 */
@Service
@RequiredArgsConstructor
public class CalendarService {

  private final EventRepository eventRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Returns all events for a user within the given date range.
   *
   * @param userId the user's UUID
   * @param start  range start (inclusive)
   * @param end    range end (exclusive)
   * @return list of events in the range
   */
  public List<Event> getCalendarEvents(UUID userId, OffsetDateTime start, OffsetDateTime end) {
    return eventRepository.findByUserIdAndDateRange(userId, start, end);
  }

  /**
   * Creates a new calendar event and publishes a {@link CalendarEventCreatedEvent}.
   *
   * @param userId      the event owner
   * @param title       event title
   * @param description event description
   * @param start       start time
   * @param end         end time
   * @return the created event
   */
  @Transactional
  public Event createEvent(UUID userId, String title, String description,
                           OffsetDateTime start, OffsetDateTime end) {
    Event event = Event.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .title(title)
            .description(description)
            .startAt(start)
            .endAt(end)
            .build();

    Event saved = eventRepository.save(event);

    eventPublisher.publishEvent(new CalendarEventCreatedEvent(
            this, saved.getId(), userId, title, start));

    return saved;
  }
}