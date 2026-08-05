package com.safo.icos.modules.workflow.tasks.application;

import com.safo.icos.modules.workflow.tasks.api.event.TaskAssignedEvent;
import com.safo.icos.modules.workflow.tasks.api.event.TaskStatusChangedEvent;
import com.safo.icos.modules.workflow.tasks.domain.model.Task;
import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;
import com.safo.icos.modules.workflow.tasks.domain.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock private TaskRepository taskRepository;
  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private TaskService taskService;

  private static final UUID ASSIGNEE_ID = UUID.randomUUID();
  private static final UUID DEPARTMENT_ID = UUID.randomUUID();
  private static final UUID TASK_ID = UUID.randomUUID();

  @Test
  void shouldCreateTaskAndPublishEvent() {
    when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Task result = taskService.createTask("Setup workspace", "Configure laptop", ASSIGNEE_ID, DEPARTMENT_ID);

    assertThat(result.getTitle()).isEqualTo("Setup workspace");
    assertThat(result.getStatus()).isEqualTo(TaskStatus.TODO);
    assertThat(result.getAssigneeId()).isEqualTo(ASSIGNEE_ID);

    ArgumentCaptor<TaskAssignedEvent> captor = ArgumentCaptor.forClass(TaskAssignedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().getTitle()).isEqualTo("Setup workspace");
    assertThat(captor.getValue().getAssigneeId()).isEqualTo(ASSIGNEE_ID);
  }

  @Test
  void shouldMoveTaskAndPublishEvent() {
    Task task = Task.builder()
            .id(TASK_ID).title("Task 1").description("Desc")
            .status(TaskStatus.TODO).assigneeId(ASSIGNEE_ID)
            .departmentId(DEPARTMENT_ID).build();

    when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
    when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Task result = taskService.moveTask(TASK_ID, TaskStatus.IN_PROGRESS);

    assertThat(result.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);

    ArgumentCaptor<TaskStatusChangedEvent> captor = ArgumentCaptor.forClass(TaskStatusChangedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().getNewStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
  }

  @Test
  void shouldThrowWhenMovingNonExistentTask() {
    UUID nonExistentId = UUID.randomUUID();
    when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.moveTask(nonExistentId, TaskStatus.DONE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not found");
  }
}