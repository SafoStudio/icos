package com.safo.icos.modules.notification.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.domain.model.Notification;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationDto createNotification(UUID userId, NotificationType type, String title, String message, Map<String, String> metadata) {
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

    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(UUID userId, int limit, int offset) {
        return notificationRepository.findByUserId(userId, limit, offset)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public NotificationDto markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        return toDto(notificationRepository.save(notification.markAsRead()));
    }

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
