package com.safo.icos.modules.calendar.api.dto;

import com.safo.icos.modules.calendar.domain.model.enums.ParticipantStatus;
import com.safo.icos.modules.identity.user.application.dto.UserResponseDto;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipantDto {
    private UUID userId;
    private ParticipantStatus status;
    private UserResponseDto user;
}
