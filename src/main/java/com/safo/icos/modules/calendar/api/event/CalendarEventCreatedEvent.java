package com.safo.icos.modules.calendar.api.event;

import java.time.OffsetDateTime;
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

  public CalendarEventCreatedEvent(Object source, UUID eventId, UUID userId,
                                   String title, OffsetDateTime startTime) {
    super(source);
    this.eventId = eventId;
    this.userId = userId;
    this.title = title;
    this.startTime = startTime;
  }
}