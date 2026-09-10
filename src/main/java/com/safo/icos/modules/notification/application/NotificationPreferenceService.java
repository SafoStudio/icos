package com.safo.icos.modules.notification.application;

import com.safo.icos.modules.notification.application.dto.NotificationPreferenceDto;
import com.safo.icos.modules.notification.domain.model.NotificationPreference;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.notification.domain.repository.NotificationPreferenceRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferenceService {

  private final NotificationPreferenceRepository preferenceRepository;

  /**
   * Retrieves notification preferences for a user, filling in default enabled state
   * for any notification types not yet explicitly saved by the user.
   */
  @Transactional(readOnly = true)
  public List<NotificationPreferenceDto> getUserPreferences(UUID userId) {
    List<NotificationPreference> existing = preferenceRepository.findByUserId(userId);
    Map<NotificationType, NotificationPreference> map = new EnumMap<>(NotificationType.class);
    for (NotificationPreference pref : existing) {
      map.put(pref.getType(), pref);
    }

    List<NotificationPreferenceDto> result = new ArrayList<>();
    for (NotificationType type : NotificationType.values()) {
      NotificationPreference pref = map.get(type);
      if (pref != null) {
        result.add(toDto(pref));
      } else {
        result.add(NotificationPreferenceDto.builder()
                .userId(userId)
                .type(type)
                .inAppEnabled(true)
                .emailEnabled(false)
                .build());
      }
    }
    return result;
  }

  /**
   * Checks whether in-app notification is enabled for a user and type.
   * Defaults to true if no preference record is found.
   */
  @Transactional(readOnly = true)
  public boolean isNotificationEnabled(UUID userId, NotificationType type) {
    return preferenceRepository.findByUserIdAndType(userId, type)
            .map(NotificationPreference::isInAppEnabled)
            .orElse(true);
  }

  /**
   * Checks whether email notification is enabled for a user and type.
   * Defaults to false if no preference record is found.
   */
  @Transactional(readOnly = true)
  public boolean isEmailNotificationEnabled(UUID userId, NotificationType type) {
    return preferenceRepository.findByUserIdAndType(userId, type)
            .map(NotificationPreference::isEmailEnabled)
            .orElse(false);
  }

  /**
   * Updates or creates a single notification preference for a user.
   */
  public NotificationPreferenceDto updatePreference(UUID userId, NotificationType type,
                                                    boolean inAppEnabled, boolean emailEnabled) {
    NotificationPreference preference = preferenceRepository.findByUserIdAndType(userId, type)
            .map(existing -> existing.update(inAppEnabled, emailEnabled))
            .orElseGet(() -> NotificationPreference.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .type(type)
                    .inAppEnabled(inAppEnabled)
                    .emailEnabled(emailEnabled)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build());

    return toDto(preferenceRepository.save(preference));
  }

  /**
   * Updates multiple preferences for a user in bulk.
   */
  public List<NotificationPreferenceDto> updateAllPreferences(UUID userId, List<NotificationPreferenceDto> dtos) {
    List<NotificationPreference> toSave = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    for (NotificationPreferenceDto dto : dtos) {
      NotificationPreference preference = preferenceRepository.findByUserIdAndType(userId, dto.getType())
              .map(existing -> existing.update(dto.isInAppEnabled(), dto.isEmailEnabled()))
              .orElseGet(() -> NotificationPreference.builder()
                      .id(UUID.randomUUID())
                      .userId(userId)
                      .type(dto.getType())
                      .inAppEnabled(dto.isInAppEnabled())
                      .emailEnabled(dto.isEmailEnabled())
                      .createdAt(now)
                      .updatedAt(now)
                      .build());
      toSave.add(preference);
    }

    return preferenceRepository.saveAll(toSave).stream()
            .map(this::toDto)
            .toList();
  }

  private NotificationPreferenceDto toDto(NotificationPreference pref) {
    return NotificationPreferenceDto.builder()
            .id(pref.getId())
            .userId(pref.getUserId())
            .type(pref.getType())
            .inAppEnabled(pref.isInAppEnabled())
            .emailEnabled(pref.isEmailEnabled())
            .build();
  }
}
