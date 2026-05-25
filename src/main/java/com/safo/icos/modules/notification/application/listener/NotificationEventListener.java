package com.safo.icos.modules.notification.application.listener;

import com.safo.icos.modules.notification.application.NotificationService;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.recruitment.api.event.CandidateHiredEvent;
import com.safo.icos.modules.recruitment.api.event.CandidateStageChangedEvent;
import com.safo.icos.modules.onboarding.api.event.OnboardingTaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskStatusChangedEvent;
import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestApprovedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestCreatedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestRejectedEvent;
import com.safo.icos.modules.user.api.UserProvider;
import com.safo.icos.modules.user.api.dto.MembershipDto;
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
    private final UserProvider userProvider;

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

    @Async
    @EventListener
    public void handleLeaveRequestCreated(LeaveRequestCreatedEvent event) {
        log.info("Handling LeaveRequestCreatedEvent for user: {}", event.getLeaveRequest().getUserId());

        // Notify manager - MembershipDto does not currently have managerId
        /*
        userProvider.findFirstActiveMembershipByUserId(event.getLeaveRequest().getUserId())
                .ifPresent(membership -> {
                    if (membership.getManagerId() != null) {
                        notificationService.createNotification(
                                membership.getManagerId(),
                                NotificationType.LEAVE_REQUEST_CREATED,
                                "New Leave Request",
                                "Employee requested leave from " + event.getLeaveRequest().getStartDate() + " to " + event.getLeaveRequest().getEndDate(),
                                Map.of("leaveRequestId", event.getLeaveRequest().getId().toString())
                        );
                    }
                });
        */
    }

    @Async
    @EventListener
    public void handleLeaveRequestApproved(LeaveRequestApprovedEvent event) {
        log.info("Handling LeaveRequestApprovedEvent for user: {}", event.getLeaveRequest().getUserId());

        notificationService.createNotification(
                event.getLeaveRequest().getUserId(),
                NotificationType.LEAVE_REQUEST_APPROVED,
                "Leave Request Approved",
                "Your leave request from " + event.getLeaveRequest().getStartDate() + " has been approved.",
                Map.of("leaveRequestId", event.getLeaveRequest().getId().toString())
        );
    }

    @Async
    @EventListener
    public void handleLeaveRequestRejected(LeaveRequestRejectedEvent event) {
        log.info("Handling LeaveRequestRejectedEvent for user: {}", event.getLeaveRequest().getUserId());

        notificationService.createNotification(
                event.getLeaveRequest().getUserId(),
                NotificationType.LEAVE_REQUEST_REJECTED,
                "Leave Request Rejected",
                "Your leave request from " + event.getLeaveRequest().getStartDate() + " has been rejected.",
                Map.of("leaveRequestId", event.getLeaveRequest().getId().toString())
        );
    }
}
