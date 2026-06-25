package com.safo.icos.modules.calendar.presentation;

import com.safo.icos.modules.calendar.api.dto.EventDto;
import com.safo.icos.modules.calendar.application.CalendarService;
import com.safo.icos.modules.calendar.infrastructure.mapper.EventMapper;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CalendarGraphQLResolver {

    private final CalendarService calendarService;
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
            @Argument String end) {
        return mapper.toDto(calendarService.createEvent(
                principal.getUserId(),
                title,
                description,
                OffsetDateTime.parse(start),
                OffsetDateTime.parse(end)
        ));
    }
}
