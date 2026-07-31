package com.safo.icos.modules.calendar.domain.model;

import com.safo.icos.modules.calendar.domain.model.enums.EventType;
import com.safo.icos.modules.calendar.domain.model.enums.SourceType;
import com.safo.icos.modules.calendar.domain.model.enums.Visibility;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a calendar event.
 */
@Getter
@Builder
public class Event {
  private final UUID id;
  private final UUID userId;
  private final String title;
  private final String description;
  private final EventType type;
  /** Source that created this event. */
  private final SourceType source;
  private final OffsetDateTime startAt;
  private final OffsetDateTime endAt;
  private final Visibility visibility;
}