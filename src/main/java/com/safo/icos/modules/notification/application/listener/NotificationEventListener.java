package com.safo.icos.modules.notification.application.listener;

import com.safo.icos.modules.calendar.api.event.CalendarEventCreatedEvent;
import com.safo.icos.modules.notification.application.NotificationService;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.talent.onboarding.api.event.OnboardingTaskAssignedEvent;
import com.safo.icos.modules.talent.recruitment.api.event.CandidateHiredEvent;
import com.safo.icos.modules.talent.recruitment.api.event.CandidateStageChangedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestApprovedEvent;
import com.safo.icos.modules.timeoff.api.event.LeaveRequestRejectedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskStatusChangedEvent;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

  private static final String KEY_TASK_ID = "taskId";
  private static final String KEY_CANDIDATE_ID = "candidateId";
  private static final String KEY_VACANCY_ID = "vacancyId";
  private static final String KEY_COMPANY_ID = "companyId";
  private static final String KEY_APPLICATION_ID = "applicationId";
  private static final String KEY_STAGE_ID = "stageId";
  private static final String KEY_EVENT_ID = "eventId";
  private static final String KEY_START_TIME = "startTime";
  private static final String KEY_LEAVE_REQUEST_ID = "leaveRequestId";
  private static final String KEY_NEW_STATUS = "newStatus";

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
                    KEY_CANDIDATE_ID, event.getCandidateId().toString(),
                    KEY_VACANCY_ID, event.getVacancyId().toString(),
                    KEY_COMPANY_ID, event.getCompanyId().toString()));
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
                    KEY_CANDIDATE_ID, event.getCandidateId().toString(),
                    KEY_APPLICATION_ID, event.getApplicationId().toString(),
                    KEY_STAGE_ID, event.getStageId().toString()));
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
            Map.of(KEY_TASK_ID, event.getTaskId().toString()));
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
            Map.of(KEY_TASK_ID, event.getTaskId().toString()));
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
                    KEY_TASK_ID, event.getTaskId().toString(),
                    KEY_NEW_STATUS, event.getNewStatus().name()));
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
                    KEY_EVENT_ID, event.getEventId().toString(),
                    KEY_START_TIME, event.getStartTime().toString()));
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
            Map.of(KEY_LEAVE_REQUEST_ID, event.getLeaveRequest().getId().toString()));
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
            Map.of(KEY_LEAVE_REQUEST_ID, event.getLeaveRequest().getId().toString()));
  }
}