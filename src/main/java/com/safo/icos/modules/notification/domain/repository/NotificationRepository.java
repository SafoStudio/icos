package com.safo.icos.modules.notification.domain.repository;

import com.safo.icos.modules.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Notification} persistence.
 */
public interface NotificationRepository {
  Notification save(Notification notification);
  Optional<Notification> findById(UUID id);
  List<Notification> findByUserId(UUID userId, int limit, int offset);
  long countUnreadByUserId(UUID userId);
  void markAllAsReadByUserId(UUID userId);
  void deleteById(UUID id);
}