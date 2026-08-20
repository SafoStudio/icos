package com.safo.icos.modules.calendar.domain.model;

import com.safo.icos.modules.calendar.domain.model.enums.ParticipantStatus;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class EventParticipant {
    private UUID userId;
    private ParticipantStatus status;
}
