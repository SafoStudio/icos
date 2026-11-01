package com.safo.icos.modules.talent.onboarding.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.talent.onboarding.api.event.OnboardingTaskAssignedEvent;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingProcess;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTask;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTaskTemplate;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTemplate;
import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingProcessStatus;
import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingTaskStatus;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingProcessRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskTemplateRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

  @Mock private OnboardingProcessRepository processRepository;
  @Mock private OnboardingTaskRepository taskRepository;
  @Mock private OnboardingTemplateRepository templateRepository;
  @Mock private OnboardingTaskTemplateRepository taskTemplateRepository;
  @Mock private UserProvider userProvider;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private OnboardingService onboardingService;

  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID TEMPLATE_ID = UUID.randomUUID();
  private static final UUID MEMBERSHIP_ID = UUID.randomUUID();
  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID PROCESS_ID = UUID.randomUUID();
  private static final UUID TASK_ID = UUID.randomUUID();

  @Test
  void shouldCreateTemplate() {
    when(templateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    OnboardingTemplate result = onboardingService.createTemplate(COMPANY_ID, "Standard", "Default");

    assertThat(result.getName()).isEqualTo("Standard");
    assertThat(result.getCompanyId()).isEqualTo(COMPANY_ID);
    verify(templateRepository).save(any());
  }

  @Test
  void shouldStartOnboardingAndCreateTasks() {
    OnboardingProcess process = OnboardingProcess.builder()
            .id(PROCESS_ID).membershipId(MEMBERSHIP_ID).templateId(TEMPLATE_ID)
            .status(OnboardingProcessStatus.NOT_STARTED)
            .startDate(LocalDate.now()).targetCompletionDate(LocalDate.now().plusWeeks(2))
            .createdAt(java.time.LocalDateTime.now()).build();

    OnboardingTaskTemplate taskTemplate = OnboardingTaskTemplate.create(
            TEMPLATE_ID, "Setup email", "Configure email", 1);

    MembershipDto membership = MembershipDto.builder()
            .id(MEMBERSHIP_ID).userId(USER_ID).active(true).build();

    when(processRepository.save(any())).thenReturn(process);
    when(userProvider.findMembershipById(MEMBERSHIP_ID)).thenReturn(Optional.of(membership));
    when(taskTemplateRepository.findByTemplateId(TEMPLATE_ID)).thenReturn(List.of(taskTemplate));
    when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    OnboardingProcess result = onboardingService.startOnboarding(MEMBERSHIP_ID, TEMPLATE_ID, LocalDate.now());

    assertThat(result.getId()).isEqualTo(PROCESS_ID);
    verify(taskRepository).save(any());

    ArgumentCaptor<OnboardingTaskAssignedEvent> captor =
            ArgumentCaptor.forClass(OnboardingTaskAssignedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().getAssigneeId()).isEqualTo(USER_ID);
    assertThat(captor.getValue().getTaskTitle()).isEqualTo("Setup email");
  }

  @Test
  void shouldThrowWhenMembershipNotFoundOnStart() {
    LocalDate startDate = LocalDate.now();

    when(userProvider.findMembershipById(MEMBERSHIP_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> onboardingService.startOnboarding(MEMBERSHIP_ID, TEMPLATE_ID, startDate))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Membership not found");
  }

  @Test
  void shouldCompleteTaskAndNotCompleteProcessWhenOtherTasksPending() {
    OnboardingTask task = OnboardingTask.builder()
            .id(TASK_ID).processId(PROCESS_ID).title("Task 1")
            .assigneeId(USER_ID).status(OnboardingTaskStatus.PENDING)
            .dueDate(LocalDate.now().plusDays(5)).createdAt(java.time.LocalDateTime.now()).build();

    OnboardingTask anotherTask = OnboardingTask.builder()
            .id(UUID.randomUUID()).processId(PROCESS_ID).title("Task 2")
            .assigneeId(USER_ID).status(OnboardingTaskStatus.PENDING)
            .dueDate(LocalDate.now().plusDays(10)).createdAt(java.time.LocalDateTime.now()).build();

    when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
    when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(taskRepository.findByProcessId(PROCESS_ID)).thenReturn(List.of(task, anotherTask));

    onboardingService.completeTask(TASK_ID);

    verify(taskRepository).save(any());
    verify(processRepository, times(0)).save(any());
  }

  @Test
  void shouldCompleteProcessWhenAllTasksDone() {
    OnboardingTask task = OnboardingTask.builder()
            .id(TASK_ID).processId(PROCESS_ID).title("Last task")
            .assigneeId(USER_ID).status(OnboardingTaskStatus.PENDING)
            .dueDate(LocalDate.now().plusDays(5)).createdAt(java.time.LocalDateTime.now()).build();

    OnboardingTask completedTask = OnboardingTask.builder()
            .id(TASK_ID).processId(PROCESS_ID).title("Last task")
            .assigneeId(USER_ID).status(OnboardingTaskStatus.DONE)
            .dueDate(LocalDate.now().plusDays(5))
            .completedAt(java.time.LocalDateTime.now())
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now()).build();

    OnboardingProcess process = OnboardingProcess.builder()
            .id(PROCESS_ID).membershipId(MEMBERSHIP_ID).templateId(TEMPLATE_ID)
            .status(OnboardingProcessStatus.IN_PROGRESS)
            .startDate(LocalDate.now()).targetCompletionDate(LocalDate.now().plusWeeks(2))
            .createdAt(java.time.LocalDateTime.now()).build();

    when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
    when(taskRepository.save(any())).thenReturn(completedTask);
    when(taskRepository.findByProcessId(PROCESS_ID)).thenReturn(List.of(completedTask));
    when(processRepository.findById(PROCESS_ID)).thenReturn(Optional.of(process));
    when(processRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    onboardingService.completeTask(TASK_ID);

    verify(processRepository).save(any());
  }

  @Test
  void shouldThrowWhenCompletingNonExistentTask() {
    when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> onboardingService.completeTask(TASK_ID))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Task not found");
  }

  @Test
  void shouldReturnProcessTasks() {
    OnboardingTask task1 = OnboardingTask.builder()
            .id(UUID.randomUUID()).processId(PROCESS_ID).title("Task 1")
            .assigneeId(USER_ID).status(OnboardingTaskStatus.PENDING)
            .dueDate(LocalDate.now().plusDays(5)).createdAt(java.time.LocalDateTime.now()).build();
    OnboardingTask task2 = OnboardingTask.builder()
            .id(UUID.randomUUID()).processId(PROCESS_ID).title("Task 2")
            .assigneeId(USER_ID).status(OnboardingTaskStatus.DONE)
            .dueDate(LocalDate.now().plusDays(3)).createdAt(java.time.LocalDateTime.now()).build();

    when(taskRepository.findByProcessId(PROCESS_ID)).thenReturn(List.of(task1, task2));

    List<OnboardingTask> tasks = onboardingService.getProcessTasks(PROCESS_ID);

    assertThat(tasks).hasSize(2);
    assertThat(tasks).extracting(OnboardingTask::getTitle).containsExactly("Task 1", "Task 2");
  }
}