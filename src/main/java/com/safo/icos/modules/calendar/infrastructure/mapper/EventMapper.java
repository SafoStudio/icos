package com.safo.icos.modules.calendar.infrastructure.mapper;

import com.safo.icos.modules.calendar.api.dto.EventDto;
import com.safo.icos.modules.calendar.api.dto.EventParticipantDto;
import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.model.EventParticipant;
import com.safo.icos.modules.calendar.infrastructure.repository.entity.EventJpaEntity;
import com.safo.icos.modules.calendar.infrastructure.repository.entity.EventParticipantEmbeddable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toDomain(EventJpaEntity entity) {
        if (entity == null) return null;
        List<EventParticipant> participants = entity.getParticipants() != null
                ? entity.getParticipants().stream()
                    .map(p -> EventParticipant.builder()
                            .userId(p.getUserId())
                            .status(p.getStatus())
                            .build())
                    .collect(Collectors.toList())
                : new ArrayList<>();
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
                .participants(participants)
                .build();
    }

    public EventJpaEntity toEntity(Event event) {
        if (event == null) return null;
        Set<EventParticipantEmbeddable> participants = event.getParticipants() != null
                ? event.getParticipants().stream()
                    .map(p -> EventParticipantEmbeddable.builder()
                            .userId(p.getUserId())
                            .status(p.getStatus())
                            .build())
                    .collect(Collectors.toSet())
                : new HashSet<>();
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
                .participants(participants)
                .build();
    }

    public EventDto toDto(Event domain) {
        if (domain == null) return null;
        List<EventParticipantDto> participantDetails = domain.getParticipants() != null
                ? domain.getParticipants().stream()
                    .map(p -> EventParticipantDto.builder()
                            .userId(p.getUserId())
                            .status(p.getStatus())
                            .build())
                    .collect(Collectors.toList())
                : List.of();
        List<java.util.UUID> participantIds = domain.getParticipantIds();
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
                .participantIds(participantIds)
                .participantDetails(participantDetails)
                .build();
    }
}
