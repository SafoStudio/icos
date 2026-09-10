package com.safo.icos.modules.notification.domain.repository;

import com.safo.icos.modules.notification.domain.model.NotificationPreference;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link NotificationPreference} persistence.
 */
public interface NotificationPreferenceRepository {
  NotificationPreference save(NotificationPreference preference);
  List<NotificationPreference> saveAll(List<NotificationPreference> preferences);
  Optional<NotificationPreference> findByUserIdAndType(UUID userId, NotificationType type);
  List<NotificationPreference> findByUserId(UUID userId);
}
