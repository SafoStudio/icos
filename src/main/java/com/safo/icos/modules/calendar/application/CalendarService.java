package com.safo.icos.modules.calendar.application;

import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.model.EventParticipant;
import com.safo.icos.modules.calendar.domain.model.enums.EventType;
import com.safo.icos.modules.calendar.domain.model.enums.ParticipantStatus;
import com.safo.icos.modules.calendar.domain.model.enums.SourceType;
import com.safo.icos.modules.calendar.domain.model.enums.Visibility;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for calendar event management.
 * Provides operations for creating, retrieving, updating, and deleting calendar events.
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
   * The event organizer is automatically added as an ACCEPTED participant.
   * Other participants are added with PENDING status.
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
    List<EventParticipant> participants = new ArrayList<>();
    // Creator is always an ACCEPTED participant
    participants.add(EventParticipant.builder()
            .userId(userId)
            .status(ParticipantStatus.ACCEPTED)
            .build());

    if (participantIds != null) {
      for (UUID pId : participantIds) {
        if (!pId.equals(userId)) {
          participants.add(EventParticipant.builder()
                  .userId(pId)
                  .status(ParticipantStatus.PENDING)
                  .build());
        }
      }
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
            .participants(participants)
            .build();

    Event saved = eventRepository.save(event);

    List<UUID> participantUserIds = participants.stream()
            .map(EventParticipant::getUserId)
            .filter(pId -> !pId.equals(userId))
            .toList();

    eventPublisher.publishEvent(new CalendarEventCreatedEvent(
            this, saved.getId(), userId, title, start, participantUserIds));

    return saved;
  }

  /**
   * Updates an existing calendar event. Only the organizer (creator) can update the event.
   * Preserves existing RSVP statuses for retained participants.
   *
   * @param userId         the UUID of the requesting user (must be organizer)
   * @param eventId        the UUID of the event to update
   * @param title          the updated title
   * @param description    the updated description
   * @param start          the updated start time
   * @param end            the updated end time
   * @param participantIds the updated list of participant UUIDs
   * @return the updated {@link Event}
   */
  @Transactional
  public Event updateEvent(UUID userId, UUID eventId, String title, String description,
                           OffsetDateTime start, OffsetDateTime end,
                           List<UUID> participantIds) {
    Event existing = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

    if (!existing.getUserId().equals(userId)) {
      throw new IllegalStateException("Only the event organizer can update the event.");
    }

    // Map existing participant statuses to retain RSVP responses
    Map<UUID, ParticipantStatus> existingStatuses = new HashMap<>();
    if (existing.getParticipants() != null) {
      for (EventParticipant p : existing.getParticipants()) {
        existingStatuses.put(p.getUserId(), p.getStatus());
      }
    }

    List<EventParticipant> updatedParticipants = new ArrayList<>();
    // Creator is always ACCEPTED
    updatedParticipants.add(EventParticipant.builder()
            .userId(userId)
            .status(ParticipantStatus.ACCEPTED)
            .build());

    if (participantIds != null) {
      for (UUID pId : participantIds) {
        if (!pId.equals(userId)) {
          ParticipantStatus currentStatus = existingStatuses.getOrDefault(pId, ParticipantStatus.PENDING);
          updatedParticipants.add(EventParticipant.builder()
                  .userId(pId)
                  .status(currentStatus)
                  .build());
        }
      }
    }

    Event updated = Event.builder()
            .id(existing.getId())
            .userId(existing.getUserId())
            .title(title)
            .description(description)
            .type(existing.getType() != null ? existing.getType() : EventType.MEETING)
            .source(existing.getSource() != null ? existing.getSource() : SourceType.MANUAL)
            .visibility(existing.getVisibility() != null ? existing.getVisibility() : Visibility.PUBLIC)
            .startAt(start)
            .endAt(end)
            .participants(updatedParticipants)
            .build();

    return eventRepository.save(updated);
  }

  /**
   * Updates attendance RSVP status for a participant in the event (e.g. ACCEPTED or DECLINED).
   *
   * @param userId  the UUID of the attendee
   * @param eventId the UUID of the event
   * @param status  the new RSVP status
   * @return the updated {@link Event}
   */
  @Transactional
  public Event updateAttendance(UUID userId, UUID eventId, ParticipantStatus status) {
    Event existing = eventRepository.findById(eventId)
            .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

    List<EventParticipant> participants = new ArrayList<>();
    boolean found = false;

    if (existing.getParticipants() != null) {
      for (EventParticipant p : existing.getParticipants()) {
        if (p.getUserId().equals(userId)) {
          participants.add(EventParticipant.builder()
                  .userId(userId)
                  .status(status)
                  .build());
          found = true;
        } else {
          participants.add(p);
        }
      }
    }

    if (!found) {
      // If user was organizer or invited without entry
      participants.add(EventParticipant.builder()
              .userId(userId)
              .status(status)
              .build());
    }

    Event updated = Event.builder()
            .id(existing.getId())
            .userId(existing.getUserId())
            .title(existing.getTitle())
            .description(existing.getDescription())
            .type(existing.getType())
            .source(existing.getSource())
            .visibility(existing.getVisibility())
            .startAt(existing.getStartAt())
            .endAt(existing.getEndAt())
            .participants(participants)
            .build();

    return eventRepository.save(updated);
  }

  /**
   * Deletes a calendar event by its ID.
   * Only the organizer can delete the event.
   *
   * @param userId  the UUID of the user requesting deletion (for authorization)
   * @param eventId the UUID of the event to delete
   * @return {@code true} if the event was successfully deleted, {@code false} if the event was not found
   */
  @Transactional
  public boolean deleteEvent(UUID userId, UUID eventId) {
    return eventRepository.findById(eventId).map(event -> {
      if (!event.getUserId().equals(userId)) {
        throw new IllegalStateException("Only the event organizer can delete the event.");
      }
      eventRepository.deleteById(eventId);
      return true;
    }).orElse(false);
  }
}