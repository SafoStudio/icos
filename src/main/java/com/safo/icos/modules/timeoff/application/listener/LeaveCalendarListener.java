package com.safo.icos.modules.timeoff.application.listener;

import com.safo.icos.modules.calendar.application.CalendarService;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestApprovedEvent;
import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import com.safo.icos.modules.timeoff.domain.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveCalendarListener {

    private final CalendarService calendarService;
    private final LeaveTypeRepository leaveTypeRepository;

    @Async
    @EventListener
    public void handleLeaveApproved(LeaveRequestApprovedEvent event) {
        LeaveRequest request = event.getLeaveRequest();
        log.info("Creating calendar event for approved leave: {}", request.getId());

        String leaveTypeName = leaveTypeRepository.findById(request.getLeaveTypeId())
                .map(lt -> lt.getName())
                .orElse("Leave");

        // Create calendar event for the employee
        // We set it from 00:00 of start date to 23:59 of end date
        OffsetDateTime start = request.getStartDate().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = request.getEndDate().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

        calendarService.createEvent(
                request.getUserId(),
                "Time Off: " + leaveTypeName,
                request.getReason(),
                start,
                end
        );
    }
}
