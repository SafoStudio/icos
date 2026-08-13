package com.safo.icos.modules.calendar.infrastructure.repository.adapter;

import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;
import com.safo.icos.modules.calendar.infrastructure.mapper.EventMapper;
import com.safo.icos.modules.calendar.infrastructure.repository.jpa.EventJpaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
        return jpaRepository.findByUserIdOrParticipantIdAndDateRange(userId, start, end)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Event> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Event save(Event event) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(event)));
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
