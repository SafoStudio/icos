package com.safo.icos.modules.calendar.application;

import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import com.safo.icos.modules.calendar.domain.model.Event;
import com.safo.icos.modules.calendar.domain.model.EventParticipant;
import com.safo.icos.modules.calendar.domain.model.enums.ParticipantStatus;
import com.safo.icos.modules.calendar.domain.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

  @Mock private EventRepository eventRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private CalendarService calendarService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID EVENT_ID = UUID.randomUUID();

  @Test
  void shouldCreateEventAndPublishEvent() {
    when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    OffsetDateTime start = OffsetDateTime.now();
    OffsetDateTime end = start.plusHours(1);
    UUID otherUserId = UUID.randomUUID();
    List<UUID> participants = List.of(USER_ID, otherUserId);

    Event result = calendarService.createEvent(
            USER_ID,
            "Interview",
            "Tech interview",
            start,
            end,
            participants
    );

    assertThat(result.getUserId()).isEqualTo(USER_ID);
    assertThat(result.getTitle()).isEqualTo("Interview");
    assertThat(result.getDescription()).isEqualTo("Tech interview");
    assertThat(result.getStartAt()).isEqualTo(start);
    assertThat(result.getEndAt()).isEqualTo(end);
    assertThat(result.getParticipantIds()).containsExactlyInAnyOrder(USER_ID, otherUserId);

    verify(eventRepository).save(any());

    ArgumentCaptor<CalendarEventCreatedEvent> captor =
            ArgumentCaptor.forClass(CalendarEventCreatedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());

    CalendarEventCreatedEvent event = captor.getValue();
    assertThat(event.getUserId()).isEqualTo(USER_ID);
    assertThat(event.getTitle()).isEqualTo("Interview");
    assertThat(event.getStartTime()).isEqualTo(start);
  }

  @Test
  void shouldCreateEventAndAddOrganizerAsParticipantIfNotInList() {
    when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    OffsetDateTime start = OffsetDateTime.now();
    OffsetDateTime end = start.plusHours(1);
    UUID otherParticipant = UUID.randomUUID();
    List<UUID> participants = List.of(otherParticipant);

    Event result = calendarService.createEvent(
            USER_ID,
            "Meeting",
            "Team meeting",
            start,
            end,
            participants
    );

    assertThat(result.getParticipantIds())
            .containsExactlyInAnyOrder(USER_ID, otherParticipant);
  }

  @Test
  void shouldCreateEventWithOnlyOrganizerWhenNoParticipantsProvided() {
    when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    OffsetDateTime start = OffsetDateTime.now();
    OffsetDateTime end = start.plusHours(1);

    Event result = calendarService.createEvent(
            USER_ID,
            "Personal Task",
            "Work on project",
            start,
            end,
            null
    );

    assertThat(result.getParticipantIds()).containsExactly(USER_ID);
  }

  @Test
  void shouldReturnEventsForDateRange() {
    OffsetDateTime start = OffsetDateTime.now();
    OffsetDateTime end = start.plusDays(7);

    Event event1 = Event.builder()
            .id(UUID.randomUUID()).userId(USER_ID)
            .title("Event 1").startAt(start.plusDays(1)).endAt(start.plusDays(1).plusHours(1))
            .participants(List.of(EventParticipant.builder().userId(USER_ID).status(ParticipantStatus.ACCEPTED).build()))
            .build();
    Event event2 = Event.builder()
            .id(UUID.randomUUID()).userId(USER_ID)
            .title("Event 2").startAt(start.plusDays(3)).endAt(start.plusDays(3).plusHours(2))
            .participants(List.of(EventParticipant.builder().userId(USER_ID).status(ParticipantStatus.ACCEPTED).build()))
            .build();

    when(eventRepository.findByUserIdAndDateRange(USER_ID, start, end))
            .thenReturn(List.of(event1, event2));

    List<Event> events = calendarService.getCalendarEvents(USER_ID, start, end);

    assertThat(events).hasSize(2);
    assertThat(events).extracting(Event::getTitle).containsExactly("Event 1", "Event 2");
  }

  @Test
  void shouldUpdateAttendanceStatus() {
    Event existing = Event.builder()
            .id(EVENT_ID)
            .userId(USER_ID)
            .title("Team Standup")
            .startAt(OffsetDateTime.now())
            .endAt(OffsetDateTime.now().plusHours(1))
            .participants(List.of(
                    EventParticipant.builder().userId(USER_ID).status(ParticipantStatus.ACCEPTED).build(),
                    EventParticipant.builder().userId(UUID.fromString("00000000-0000-0000-0000-000000000002")).status(ParticipantStatus.PENDING).build()
            ))
            .build();

    when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(existing));
    when(eventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    UUID attendeeId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    Event updated = calendarService.updateAttendance(attendeeId, EVENT_ID, ParticipantStatus.ACCEPTED);

    EventParticipant participant = updated.getParticipants().stream()
            .filter(p -> p.getUserId().equals(attendeeId))
            .findFirst()
            .orElseThrow();
    assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.ACCEPTED);
  }
}