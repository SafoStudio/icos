package com.safo.icos.modules.calendar.infrastructure.mapper;

import com.safo.icos.modules.calendar.api.dto.EventDto;
import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.infrastructure.repository.entity.EventJpaEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toDomain(EventJpaEntity entity) {
        if (entity == null) return null;
        List<java.util.UUID> participants = entity.getParticipantIds() != null
                ? new ArrayList<>(entity.getParticipantIds())
                : List.of();
        return Event.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .source(entity.getSource())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .visibility(entity.getVisibility())
                .participantIds(participants)
                .build();
    }

    public EventJpaEntity toEntity(Event event) {
        if (event == null) return null;
        return EventJpaEntity.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .title(event.getTitle())
                .description(event.getDescription())
                .type(event.getType())
                .source(event.getSource())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .visibility(event.getVisibility())
                .participantIds(event.getParticipantIds() != null ? new HashSet<>(event.getParticipantIds()) : new HashSet<>())
                .build();
    }

    public EventDto toDto(Event domain) {
        if (domain == null) return null;
        return EventDto.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .type(domain.getType())
                .source(domain.getSource())
                .startAt(domain.getStartAt())
                .endAt(domain.getEndAt())
                .visibility(domain.getVisibility())
                .participantIds(domain.getParticipantIds() != null ? domain.getParticipantIds() : List.of())
                .build();
    }
}
