package com.safo.icos.modules.notification.presentation;

import com.safo.icos.modules.notification.application.NotificationPreferenceService;
import com.safo.icos.modules.notification.application.NotificationService;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
import com.safo.icos.modules.notification.application.dto.NotificationPreferenceDto;
import com.safo.icos.modules.notification.application.dto.NotificationPreferenceInput;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class NotificationGraphQLResolver {

    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;

    @QueryMapping
    public List<NotificationDto> notifications(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument int limit, @Argument int offset) {
        return notificationService.getUserNotifications(principal.getUserId(), limit, offset);
    }

    @QueryMapping
    public long unreadNotificationCount(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return notificationService.getUnreadCount(principal.getUserId());
    }

    @QueryMapping
    public List<NotificationPreferenceDto> notificationPreferences(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return preferenceService.getUserPreferences(principal.getUserId());
    }

    @MutationMapping
    public NotificationDto markNotificationAsRead(@Argument UUID id) {
        return notificationService.markAsRead(id);
    }

    @MutationMapping
    public boolean markAllNotificationsAsRead(@AuthenticationPrincipal IcosUserPrincipal principal) {
        notificationService.markAllAsRead(principal.getUserId());
        return true;
    }

    @MutationMapping
    public boolean deleteNotification(@Argument UUID id) {
        notificationService.deleteNotification(id);
        return true;
    }

    @MutationMapping
    public NotificationPreferenceDto updateNotificationPreference(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument String type,
            @Argument boolean inAppEnabled,
            @Argument boolean emailEnabled) {
        return preferenceService.updatePreference(
                principal.getUserId(),
                NotificationType.valueOf(type),
                inAppEnabled,
                emailEnabled);
    }

    @MutationMapping
    public List<NotificationPreferenceDto> updateNotificationPreferences(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @Argument List<NotificationPreferenceInput> preferences) {
        List<NotificationPreferenceDto> dtos = preferences.stream()
                .map(p -> NotificationPreferenceDto.builder()
                        .userId(principal.getUserId())
                        .type(p.type())
                        .inAppEnabled(p.inAppEnabled())
                        .emailEnabled(p.emailEnabled())
                        .build())
                .toList();
        return preferenceService.updateAllPreferences(principal.getUserId(), dtos);
    }
}
