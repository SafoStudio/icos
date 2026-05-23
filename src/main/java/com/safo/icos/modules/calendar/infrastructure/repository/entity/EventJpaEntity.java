package com.safo.icos.modules.calendar.infrastructure.repository.entity;

import com.safo.icos.modules.calendar.domain.model.enums.EventType;
import com.safo.icos.modules.calendar.domain.model.enums.SourceType;
import com.safo.icos.modules.calendar.domain.model.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventJpaEntity {
    @Id
    private UUID id;
    private UUID userId;
    private String title;
    
    @Enumerated(EnumType.STRING)
    private EventType type;
    
    @Enumerated(EnumType.STRING)
    private SourceType source;
    
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
}
