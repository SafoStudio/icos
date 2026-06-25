package com.safo.icos.modules.calendar.infrastructure.repository.jpa;

import com.safo.icos.modules.calendar.infrastructure.repository.entity.EventJpaEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, UUID> {
    List<EventJpaEntity> findByUserIdAndStartAtBeforeAndEndAtAfter(UUID userId, OffsetDateTime end, OffsetDateTime start);
}
