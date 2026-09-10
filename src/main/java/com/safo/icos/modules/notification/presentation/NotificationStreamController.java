package com.safo.icos.modules.notification.presentation;

import com.safo.icos.modules.notification.infrastructure.sse.NotificationBroadcaster;
import com.safo.icos.security.context.IcosUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Controller exposing SSE endpoint for real-time notification streaming.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationStreamController {

  private final NotificationBroadcaster broadcaster;

  @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter streamNotifications(@AuthenticationPrincipal IcosUserPrincipal principal) {
    if (principal == null) {
      throw new IllegalStateException("Authentication required for notifications stream");
    }
    return broadcaster.subscribe(principal.getUserId());
  }
}
