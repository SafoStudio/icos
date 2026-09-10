package com.safo.icos.modules.notification.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationRepository;

import com.safo.icos.modules.notification.infrastructure.sse.NotificationBroadcaster;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for notification management.
 * Handles creation, retrieval, and read-state updates with subscription preference checks
 * and real-time SSE streaming.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationPreferenceService preferenceService;
  private final NotificationBroadcaster broadcaster;

  /**
   * Creates a new notification for a user if their notification preferences allow it.
   * Also broadcasts the notification via real-time stream.
   *
   * @param userId   the recipient's UUID
   * @param type     notification type
   * @param title    notification title
   * @param message  notification body
   * @param metadata optional key-value pairs (e.g., related entity IDs)
   * @return the created notification DTO, or null if the user is unsubscribed
   */
  public NotificationDto createNotification(UUID userId, NotificationType type,
                                            String title, String message,
                                            Map<String, String> metadata) {
    if (!preferenceService.isNotificationEnabled(userId, type)) {
      log.debug("Notification of type {} skipped for user {} due to user preferences", type, userId);
      return null;
    }

    Notification notification = Notification.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .type(type)
            .title(title)
            .message(message)
            .metadata(metadata)
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();

    NotificationDto dto = toDto(notificationRepository.save(notification));
    broadcaster.broadcast(userId, dto);
    return dto;
  }

  /**
   * Returns paginated notifications for a user.
   *
   * @param userId the recipient's UUID
   * @param limit  max number of notifications
   * @param offset pagination offset
   * @return list of notification DTOs
   */
  @Transactional(readOnly = true)
  public List<NotificationDto> getUserNotifications(UUID userId, int limit, int offset) {
    return notificationRepository.findByUserId(userId, limit, offset)
            .stream()
            .map(this::toDto)
            .toList();
  }

  /**
   * Returns the count of unread notifications for a user.
   *
   * @param userId the recipient's UUID
   * @return number of unread notifications
   */
  @Transactional(readOnly = true)
  public long getUnreadCount(UUID userId) {
    return notificationRepository.countUnreadByUserId(userId);
  }

  /**
   * Marks a single notification as read.
   *
   * @param notificationId the notification UUID
   * @return the updated notification DTO
   * @throws NotFoundException if the notification is not found
   */
  public NotificationDto markAsRead(UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotFoundException("Notification not found"));

    return toDto(notificationRepository.save(notification.markAsRead()));
  }

  /**
   * Marks all notifications as read for a user.
   *
   * @param userId the recipient's UUID
   */
  public void markAllAsRead(UUID userId) {
    notificationRepository.markAllAsReadByUserId(userId);
  }

  /**
   * Deletes a notification by its ID.
   *
   * @param notificationId the notification UUID
   */
  public void deleteNotification(UUID notificationId) {
    notificationRepository.deleteById(notificationId);
  }

  private NotificationDto toDto(Notification notification) {
    return NotificationDto.builder()
            .id(notification.getId())
            .userId(notification.getUserId())
            .type(notification.getType())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .metadata(notification.getMetadata())
            .isRead(notification.isRead())
            .readAt(notification.getReadAt())
            .createdAt(notification.getCreatedAt())
            .build();
  }
}