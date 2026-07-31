package com.safo.icos.modules.calendar.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * DTO representing a calendar event.
 */
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