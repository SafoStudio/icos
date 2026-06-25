package com.safo.icos.modules.calendar.infrastructure.repository.adapter;

import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;
import com.safo.icos.modules.calendar.infrastructure.mapper.EventMapper;
import com.safo.icos.modules.calendar.infrastructure.repository.jpa.EventJpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepositoryAdapter implements EventRepository {
    private final EventJpaRepository jpaRepository;
    private final EventMapper mapper;

    @Override
    public List<Event> findByUserIdAndDateRange(UUID userId, OffsetDateTime start, OffsetDateTime end) {
        return jpaRepository.findByUserIdAndStartAtBeforeAndEndAtAfter(userId, end, start)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Event save(Event event) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(event)));
    }
}
