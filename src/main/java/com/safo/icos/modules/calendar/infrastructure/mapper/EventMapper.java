package com.safo.icos.modules.calendar.infrastructure.mapper;

import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.infrastructure.repository.entity.EventJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event toDomain(EventJpaEntity entity) {
        return Event.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .type(entity.getType())
                .source(entity.getSource())
                .startAt(entity.getStartAt())
                .endAt(entity.getEndAt())
                .visibility(entity.getVisibility())
                .build();
    }

    public EventJpaEntity toEntity(Event event) {
        return EventJpaEntity.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .title(event.getTitle())
                .type(event.getType())
                .source(event.getSource())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .visibility(event.getVisibility())
                .build();
    }
}
