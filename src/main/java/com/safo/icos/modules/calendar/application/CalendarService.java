package com.safo.icos.modules.calendar.application;

import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.model.enums.EventType;
import com.safo.icos.modules.calendar.domain.model.enums.SourceType;
import com.safo.icos.modules.calendar.domain.model.enums.Visibility;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for calendar event management.
 * Provides operations for creating, retrieving, and deleting calendar events.
 */
@Service
@RequiredArgsConstructor
public class CalendarService {

  private final EventRepository eventRepository;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Retrieves all calendar events for a specific user within the given date range.
   * Returns events where the user is either the organizer or a participant.
   *
   * @param userId the UUID of the user whose events to retrieve
   * @param start  the start of the date range (inclusive)
   * @param end    the end of the date range (exclusive)
   * @return a list of {@link Event} objects within the specified date range
   */
  public List<Event> getCalendarEvents(UUID userId, OffsetDateTime start, OffsetDateTime end) {
    return eventRepository.findByUserIdAndDateRange(userId, start, end);
  }

  /**
   * Creates a new calendar event and publishes a {@link CalendarEventCreatedEvent}.
   * The event organizer is automatically added as a participant if not already in the list.
   *
   * @param userId         the UUID of the event organizer/owner
   * @param title          the title of the event
   * @param description    the description or details of the event
   * @param start          the start date and time of the event
   * @param end            the end date and time of the event
   * @param participantIds list of participant user UUIDs (can be null or empty)
   * @return the created {@link Event} with generated ID and metadata
   */
  @Transactional
  public Event createEvent(UUID userId, String title, String description,
                           OffsetDateTime start, OffsetDateTime end,
                           List<UUID> participantIds) {
    List<UUID> participants = participantIds != null ? new ArrayList<>(participantIds) : new ArrayList<>();
    if (!participants.contains(userId)) {
      participants.add(userId);
    }

    Event event = Event.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .title(title)
            .description(description)
            .type(EventType.MEETING)
            .source(SourceType.MANUAL)
            .visibility(Visibility.PUBLIC)
            .startAt(start)
            .endAt(end)
            .participantIds(participants)
            .build();

    Event saved = eventRepository.save(event);

    eventPublisher.publishEvent(new CalendarEventCreatedEvent(
            this, saved.getId(), userId, title, start));

    return saved;
  }

  /**
   * Deletes a calendar event by its ID.
   * The deletion is only performed if the event exists.
   *
   * @param userId  the UUID of the user requesting deletion (for authorization)
   * @param eventId the UUID of the event to delete
   * @return {@code true} if the event was successfully deleted, {@code false} if the event was not found
   */
  @Transactional
  public boolean deleteEvent(UUID userId, UUID eventId) {
    return eventRepository.findById(eventId).map(event -> {
      eventRepository.deleteById(eventId);
      return true;
    }).orElse(false);
  }
}