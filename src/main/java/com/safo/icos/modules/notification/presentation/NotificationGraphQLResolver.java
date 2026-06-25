package com.safo.icos.modules.notification.presentation;

import com.safo.icos.modules.notification.application.NotificationService;
import com.safo.icos.modules.notification.application.dto.NotificationDto;
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

    @QueryMapping
    public List<NotificationDto> notifications(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument int limit, @Argument int offset) {
        return notificationService.getUserNotifications(principal.getUserId(), limit, offset);
    }

    @QueryMapping
    public long unreadNotificationCount(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return notificationService.getUnreadCount(principal.getUserId());
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
}
