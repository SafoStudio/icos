package com.safo.icos.modules.notification.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationRepository;
import com.safo.icos.modules.notification.infrastructure.sse.NotificationBroadcaster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;
  @Mock private NotificationPreferenceService preferenceService;
  @Mock private NotificationBroadcaster broadcaster;

  @InjectMocks
  private NotificationService notificationService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID NOTIFICATION_ID = UUID.randomUUID();

  @Test
  void shouldCreateNotificationAndBroadcast() {
    when(preferenceService.isNotificationEnabled(USER_ID, NotificationType.GENERAL_MESSAGE))
            .thenReturn(true);
    when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    NotificationDto result = notificationService.createNotification(
            USER_ID, NotificationType.GENERAL_MESSAGE, "Title", "Message",
            Map.of("key", "val"));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    Notification saved = captor.getValue();
    assertThat(saved.getUserId()).isEqualTo(USER_ID);
    assertThat(saved.getType()).isEqualTo(NotificationType.GENERAL_MESSAGE);
    assertThat(saved.getTitle()).isEqualTo("Title");
    assertThat(saved.getMessage()).isEqualTo("Message");
    assertThat(saved.getMetadata()).containsEntry("key", "val");
    assertThat(saved.isRead()).isFalse();
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();

    assertThat(result.getUserId()).isEqualTo(USER_ID);
    assertThat(result.getType()).isEqualTo(NotificationType.GENERAL_MESSAGE);
    assertThat(result.getTitle()).isEqualTo("Title");
    assertThat(result.isRead()).isFalse();

    verify(broadcaster).broadcast(eq(USER_ID), any(NotificationDto.class));
  }

  @Test
  void shouldSkipNotificationWhenUserUnsubscribed() {
    when(preferenceService.isNotificationEnabled(USER_ID, NotificationType.GENERAL_MESSAGE))
            .thenReturn(false);

    NotificationDto result = notificationService.createNotification(
            USER_ID, NotificationType.GENERAL_MESSAGE, "Title", "Message", Map.of());

    assertThat(result).isNull();
    verify(notificationRepository, never()).save(any());
    verifyNoInteractions(broadcaster);
  }

  @Test
  void shouldGetUserNotifications() {
    Notification n1 = Notification.builder()
            .id(UUID.randomUUID()).userId(USER_ID)
            .type(NotificationType.GENERAL_MESSAGE)
            .title("N1").message("M1").isRead(false)
            .createdAt(LocalDateTime.now()).build();
    Notification n2 = Notification.builder()
            .id(UUID.randomUUID()).userId(USER_ID)
            .type(NotificationType.TASK_ASSIGNED)
            .title("N2").message("M2").isRead(true).readAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now()).build();

    when(notificationRepository.findByUserId(USER_ID, 10, 0))
            .thenReturn(List.of(n1, n2));

    List<NotificationDto> result = notificationService.getUserNotifications(USER_ID, 10, 0);

    assertThat(result).hasSize(2);
    assertThat(result).extracting(NotificationDto::getTitle).containsExactly("N1", "N2");
    assertThat(result).extracting(NotificationDto::isRead).containsExactly(false, true);

    verify(notificationRepository).findByUserId(USER_ID, 10, 0);
  }

  @Test
  void shouldGetUnreadCount() {
    when(notificationRepository.countUnreadByUserId(USER_ID)).thenReturn(5L);

    long count = notificationService.getUnreadCount(USER_ID);

    assertThat(count).isEqualTo(5L);
    verify(notificationRepository).countUnreadByUserId(USER_ID);
  }

  @Test
  void shouldMarkAsRead() {
    Notification notification = Notification.builder()
            .id(NOTIFICATION_ID).userId(USER_ID)
            .type(NotificationType.GENERAL_MESSAGE)
            .title("Test").message("Message")
            .isRead(false).createdAt(LocalDateTime.now()).build();

    when(notificationRepository.findById(NOTIFICATION_ID))
            .thenReturn(Optional.of(notification));
    when(notificationRepository.save(any(Notification.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    NotificationDto result = notificationService.markAsRead(NOTIFICATION_ID);

    assertThat(result.isRead()).isTrue();
    assertThat(result.getReadAt()).isNotNull();
    assertThat(result.getId()).isEqualTo(NOTIFICATION_ID);

    verify(notificationRepository).findById(NOTIFICATION_ID);
    verify(notificationRepository).save(any(Notification.class));
  }

  @Test
  void shouldThrowWhenMarkingNonExistentAsRead() {
    when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> notificationService.markAsRead(NOTIFICATION_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");

    verify(notificationRepository, never()).save(any());
  }

  @Test
  void shouldMarkAllAsRead() {
    notificationService.markAllAsRead(USER_ID);

    verify(notificationRepository).markAllAsReadByUserId(USER_ID);
  }
}