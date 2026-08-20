package com.safo.icos.modules.calendar.api.dto;

import com.safo.icos.modules.calendar.domain.model.enums.EventType;
import com.safo.icos.modules.calendar.domain.model.enums.SourceType;
import com.safo.icos.modules.calendar.domain.model.enums.Visibility;

import java.time.OffsetDateTime;
import java.util.List;
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
  private final EventType type;
  private final SourceType source;
  private final OffsetDateTime startAt;
  private final OffsetDateTime endAt;
  private final Visibility visibility;
  private final List<UUID> participantIds;
  private final List<EventParticipantDto> participantDetails;
}