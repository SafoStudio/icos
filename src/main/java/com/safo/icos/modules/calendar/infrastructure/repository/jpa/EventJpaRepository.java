package com.safo.icos.modules.calendar.infrastructure.repository.jpa;

import com.safo.icos.modules.calendar.infrastructure.repository.entity.EventJpaEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, UUID> {
    @Query("SELECT DISTINCT e FROM EventJpaEntity e LEFT JOIN e.participants p " +
           "WHERE (e.userId = :userId OR p.userId = :userId) " +
           "AND e.startAt < :end AND e.endAt > :start")
    List<EventJpaEntity> findByUserIdOrParticipantIdAndDateRange(
            @Param("userId") UUID userId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);
}
