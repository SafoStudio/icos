package com.safo.icos.modules.notification.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

  @Test
  void shouldCreateNotification() {
    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Notification notification = Notification.builder()
            .id(id)
            .userId(userId)
            .type(NotificationType.GENERAL_MESSAGE)
            .title("Test")
            .message("Test message")
            .metadata(Map.of("key", "value"))
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

    assertThat(notification.getId()).isEqualTo(id);
    assertThat(notification.getUserId()).isEqualTo(userId);
    assertThat(notification.getType()).isEqualTo(NotificationType.GENERAL_MESSAGE);
    assertThat(notification.getTitle()).isEqualTo("Test");
    assertThat(notification.getMessage()).isEqualTo("Test message");
    assertThat(notification.getMetadata()).containsEntry("key", "value");
    assertThat(notification.isRead()).isFalse();
    assertThat(notification.getReadAt()).isNull();
  }

  @Test
  void shouldMarkAsRead() {
    Notification notification = Notification.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .type(NotificationType.GENERAL_MESSAGE)
            .title("Test")
            .message("Message")
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

    Notification read = notification.markAsRead();

    assertThat(read.isRead()).isTrue();
    assertThat(read.getReadAt()).isNotNull();
    assertThat(notification.isRead()).isFalse();
  }
}