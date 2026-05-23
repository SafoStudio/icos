package com.safo.icos.modules.calendar.application;

import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final EventRepository eventRepository;

    public List<Event> getCalendarEvents(UUID userId, OffsetDateTime start, OffsetDateTime end) {
        return eventRepository.findByUserIdAndDateRange(userId, start, end);
    }
}
