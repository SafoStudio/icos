package com.safo.icos.modules.calendar.api.event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when a new calendar event is created.
 * Used for notifications and reminders.
 */
@Getter
public class CalendarEventCreatedEvent extends ApplicationEvent {
  private final UUID eventId;
  private final UUID userId;
  private final String title;
  private final OffsetDateTime startTime;
  private final List<UUID> participantIds;

  public CalendarEventCreatedEvent(Object source, UUID eventId, UUID userId,
                                   String title, OffsetDateTime startTime,
                                   List<UUID> participantIds) {
    super(source);
    this.eventId = eventId;
    this.userId = userId;
    this.title = title;
    this.startTime = startTime;
    this.participantIds = participantIds != null ? List.copyOf(participantIds) : List.of();
  }

  public CalendarEventCreatedEvent(Object source, UUID eventId, UUID userId,
                                   String title, OffsetDateTime startTime) {
    this(source, eventId, userId, title, startTime, List.of());
  }
}