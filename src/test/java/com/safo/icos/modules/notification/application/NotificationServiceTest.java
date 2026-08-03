package com.safo.icos.modules.notification.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks
  private NotificationService notificationService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID NOTIFICATION_ID = UUID.randomUUID();

  @Test
  void shouldCreateNotification() {
    when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    NotificationDto result = notificationService.createNotification(
            USER_ID, NotificationType.GENERAL_MESSAGE, "Title", "Message", Map.of("key", "val"));

    assertThat(result.getUserId()).isEqualTo(USER_ID);
    assertThat(result.getType()).isEqualTo(NotificationType.GENERAL_MESSAGE);
    assertThat(result.getTitle()).isEqualTo("Title");
    assertThat(result.isRead()).isFalse();
    verify(notificationRepository).save(any());
  }

  @Test
  void shouldGetUserNotifications() {
    Notification n1 = Notification.builder()
            .id(UUID.randomUUID()).userId(USER_ID).type(NotificationType.GENERAL_MESSAGE)
            .title("N1").message("M1").isRead(false).createdAt(LocalDateTime.now()).build();
    Notification n2 = Notification.builder()
            .id(UUID.randomUUID()).userId(USER_ID).type(NotificationType.TASK_ASSIGNED)
            .title("N2").message("M2").isRead(true).readAt(LocalDateTime.now()).createdAt(LocalDateTime.now()).build();

    when(notificationRepository.findByUserId(USER_ID, 10, 0)).thenReturn(List.of(n1, n2));

    List<NotificationDto> result = notificationService.getUserNotifications(USER_ID, 10, 0);

    assertThat(result).hasSize(2);
    assertThat(result).extracting(NotificationDto::getTitle).containsExactly("N1", "N2");
  }

  @Test
  void shouldGetUnreadCount() {
    when(notificationRepository.countUnreadByUserId(USER_ID)).thenReturn(5L);

    long count = notificationService.getUnreadCount(USER_ID);

    assertThat(count).isEqualTo(5L);
  }

  @Test
  void shouldMarkAsRead() {
    Notification notification = Notification.builder()
            .id(NOTIFICATION_ID).userId(USER_ID).type(NotificationType.GENERAL_MESSAGE)
            .title("Test").message("Message").isRead(false).createdAt(LocalDateTime.now()).build();

    when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(notification));
    when(notificationRepository.save(any())).thenReturn(notification.markAsRead());

    NotificationDto result = notificationService.markAsRead(NOTIFICATION_ID);

    assertThat(result.isRead()).isTrue();
    assertThat(result.getReadAt()).isNotNull();
  }

  @Test
  void shouldThrowWhenMarkingNonExistentAsRead() {
    when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.markAsRead(NOTIFICATION_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldMarkAllAsRead() {
    notificationService.markAllAsRead(USER_ID);

    verify(notificationRepository).markAllAsReadByUserId(USER_ID);
  }
}