package com.safo.icos.modules.notification.application.listener;

import com.safo.icos.modules.notification.application.NotificationService;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.recruitment.api.event.CandidateHiredEvent;
import com.safo.icos.modules.recruitment.api.event.CandidateStageChangedEvent;
import com.safo.icos.modules.onboarding.api.event.OnboardingTaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskStatusChangedEvent;
import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleCandidateHired(CandidateHiredEvent event) {
        log.info("Handling CandidateHiredEvent for candidate: {}", event.getCandidateId());
        
        notificationService.createNotification(
                event.getUserId(),
                NotificationType.CANDIDATE_HIRED,
                "Welcome to the team!",
                "You have been successfully hired for the vacancy.",
                Map.of(
                        "candidateId", event.getCandidateId().toString(),
                        "vacancyId", event.getVacancyId().toString(),
                        "companyId", event.getCompanyId().toString()
                )
        );
    }

    @Async
    @EventListener
    public void handleCandidateStageChanged(CandidateStageChangedEvent event) {
        log.info("Handling CandidateStageChangedEvent for candidate: {}", event.getCandidateId());

        notificationService.createNotification(
                event.getUserId(),
                NotificationType.CANDIDATE_STAGE_CHANGED,
                "Application Stage Updated",
                "Your application stage has been updated.",
                Map.of(
                        "candidateId", event.getCandidateId().toString(),
                        "applicationId", event.getApplicationId().toString(),
                        "stageId", event.getStageId().toString()
                )
        );
    }

    @Async
    @EventListener
    public void handleOnboardingTaskAssigned(OnboardingTaskAssignedEvent event) {
        log.info("Handling OnboardingTaskAssignedEvent for task: {}", event.getTaskId());

        notificationService.createNotification(
                event.getAssigneeId(),
                NotificationType.ONBOARDING_TASK_ASSIGNED,
                "New Onboarding Task",
                "You have been assigned a new task: " + event.getTaskTitle(),
                Map.of(
                        "taskId", event.getTaskId().toString()
                )
        );
    }

    @Async
    @EventListener
    public void handleTaskAssigned(TaskAssignedEvent event) {
        log.info("Handling TaskAssignedEvent for task: {}", event.getTaskId());

        notificationService.createNotification(
                event.getAssigneeId(),
                NotificationType.TASK_ASSIGNED,
                "New Task Assigned",
                "A new task has been assigned to you: " + event.getTitle(),
                Map.of("taskId", event.getTaskId().toString())
        );
    }

    @Async
    @EventListener
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
        log.info("Handling TaskStatusChangedEvent for task: {}", event.getTaskId());

        notificationService.createNotification(
                event.getAssigneeId(),
                NotificationType.TASK_STATUS_CHANGED,
                "Task Status Updated",
                "Task '" + event.getTitle() + "' is now " + event.getNewStatus(),
                Map.of(
                        "taskId", event.getTaskId().toString(),
                        "newStatus", event.getNewStatus().name()
                )
        );
    }

    @Async
    @EventListener
    public void handleCalendarEventCreated(CalendarEventCreatedEvent event) {
        log.info("Handling CalendarEventCreatedEvent for event: {}", event.getEventId());

        notificationService.createNotification(
                event.getUserId(),
                NotificationType.CALENDAR_EVENT_CREATED,
                "New Calendar Event",
                "Event '" + event.getTitle() + "' has been added to your calendar.",
                Map.of(
                        "eventId", event.getEventId().toString(),
                        "startTime", event.getStartTime().toString()
                )
        );
    }
}
