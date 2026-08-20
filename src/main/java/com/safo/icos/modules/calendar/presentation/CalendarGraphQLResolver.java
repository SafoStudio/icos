package com.safo.icos.modules.calendar.presentation;

import com.safo.icos.modules.calendar.api.dto.EventDto;
import com.safo.icos.modules.calendar.api.dto.EventParticipantDto;
import com.safo.icos.modules.calendar.application.CalendarService;
import com.safo.icos.modules.calendar.domain.model.enums.ParticipantStatus;
import com.safo.icos.modules.calendar.infrastructure.mapper.EventMapper;
import com.safo.icos.modules.identity.user.application.dto.UserResponseDto;
import com.safo.icos.modules.identity.user.application.service.UserService;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CalendarGraphQLResolver {

    private final CalendarService calendarService;
    private final UserService userService;
    private final EventMapper mapper;

    @QueryMapping
    public List<EventDto> calendarEvents(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument String start,
            @Argument String end) {
        return calendarService.getCalendarEvents(
                principal.getUserId(),
                OffsetDateTime.parse(start),
                OffsetDateTime.parse(end)
        ).stream().map(mapper::toDto).toList();
    }

    @MutationMapping
    public EventDto createCalendarEvent(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument String title,
            @Argument String description,
            @Argument String start,
            @Argument String end,
            @Argument List<String> participantIds) {
        List<UUID> participants = participantIds != null
                ? participantIds.stream().map(UUID::fromString).toList()
                : List.of();
        return mapper.toDto(calendarService.createEvent(
                principal.getUserId(),
                title,
                description,
                OffsetDateTime.parse(start),
                OffsetDateTime.parse(end),
                participants
        ));
    }

    @MutationMapping
    public EventDto updateCalendarEvent(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument UUID id,
            @Argument String title,
            @Argument String description,
            @Argument String start,
            @Argument String end,
            @Argument List<String> participantIds) {
        List<UUID> participants = participantIds != null
                ? participantIds.stream().map(UUID::fromString).toList()
                : List.of();
        return mapper.toDto(calendarService.updateEvent(
                principal.getUserId(),
                id,
                title,
                description,
                OffsetDateTime.parse(start),
                OffsetDateTime.parse(end),
                participants
        ));
    }

    @MutationMapping
    public EventDto updateEventAttendance(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument UUID eventId,
            @Argument String status) {
        ParticipantStatus participantStatus = ParticipantStatus.valueOf(status.trim().toUpperCase());
        return mapper.toDto(calendarService.updateAttendance(
                principal.getUserId(),
                eventId,
                participantStatus
        ));
    }

    @MutationMapping
    public Boolean deleteCalendarEvent(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument UUID id) {
        return calendarService.deleteEvent(principal.getUserId(), id);
    }

    @SchemaMapping(typeName = "Event", field = "organizer")
    public UserResponseDto getOrganizer(EventDto event) {
        if (event.getUserId() == null) return null;
        try {
            return userService.getUserById(event.getUserId());
        } catch (Exception e) {
            return null;
        }
    }

    @SchemaMapping(typeName = "Event", field = "participants")
    public List<UserResponseDto> getParticipants(EventDto event) {
        if (event.getParticipantIds() == null || event.getParticipantIds().isEmpty()) {
            return List.of();
        }
        return event.getParticipantIds().stream()
                .map(id -> {
                    try {
                        return userService.getUserById(id);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @SchemaMapping(typeName = "Event", field = "participantDetails")
    public List<EventParticipantDto> getParticipantDetails(EventDto event) {
        if (event.getParticipantDetails() == null || event.getParticipantDetails().isEmpty()) {
            return List.of();
        }
        return event.getParticipantDetails();
    }

    @SchemaMapping(typeName = "EventParticipant", field = "user")
    public UserResponseDto getParticipantUser(EventParticipantDto participant) {
        if (participant.getUserId() == null) return null;
        try {
            return userService.getUserById(participant.getUserId());
        } catch (Exception e) {
            return null;
        }
    }

    @SchemaMapping(typeName = "Event", field = "currentUserStatus")
    public String getCurrentUserStatus(
            EventDto event,
            @AuthenticationPrincipal IcosUserPrincipal principal) {
        if (principal == null || principal.getUserId() == null) {
            return null;
        }
        UUID currentUserId = principal.getUserId();
        if (event.getUserId() != null && event.getUserId().equals(currentUserId)) {
            return ParticipantStatus.ACCEPTED.name();
        }
        if (event.getParticipantDetails() != null) {
            for (EventParticipantDto p : event.getParticipantDetails()) {
                if (p.getUserId() != null && p.getUserId().equals(currentUserId)) {
                    return p.getStatus() != null ? p.getStatus().name() : ParticipantStatus.PENDING.name();
                }
            }
        }
        return ParticipantStatus.PENDING.name();
    }
}
