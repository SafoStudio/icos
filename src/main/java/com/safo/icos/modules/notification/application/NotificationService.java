package com.safo.icos.modules.notification.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for notification management.
 * Handles creation, retrieval, and read-state updates.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

  private final NotificationRepository notificationRepository;

  /**
   * Creates a new notification for a user.
   *
   * @param userId   the recipient's UUID
   * @param type     notification type
   * @param title    notification title
   * @param message  notification body
   * @param metadata optional key-value pairs (e.g., related entity IDs)
   * @return the created notification DTO
   */
  public NotificationDto createNotification(UUID userId, NotificationType type,
                                            String title, String message,
                                            Map<String, String> metadata) {
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

    return toDto(notificationRepository.save(notification));
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