package com.safo.icos.modules.notification.infrastructure.sse;

import com.safo.icos.modules.notification.application.dto.NotificationDto;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Manages active Server-Sent Events (SSE) connections for real-time notification delivery.
 */
@Slf4j
@Component
public class NotificationBroadcaster {

  private final Map<UUID, List<SseEmitter>> userEmitters = new ConcurrentHashMap<>();

  /**
   * Registers a new SSE subscription for the given user.
   */
  public SseEmitter subscribe(UUID userId) {
    SseEmitter emitter = new SseEmitter(180_000L); // 3 minutes timeout

    userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

    emitter.onCompletion(() -> removeEmitter(userId, emitter));
    emitter.onTimeout(() -> removeEmitter(userId, emitter));
    emitter.onError(e -> removeEmitter(userId, emitter));

    try {
      emitter.send(SseEmitter.event().name("connected").data("connected"));
    } catch (IOException e) {
      removeEmitter(userId, emitter);
    }

    return emitter;
  }

  /**
   * Pushes a notification to all active SSE connections for the user.
   */
  public void broadcast(UUID userId, NotificationDto notification) {
    List<SseEmitter> emitters = userEmitters.get(userId);
    if (emitters == null || emitters.isEmpty()) {
      return;
    }

    for (SseEmitter emitter : emitters) {
      try {
        emitter.send(SseEmitter.event()
                .name("notification")
                .data(notification));
      } catch (Exception e) {
        removeEmitter(userId, emitter);
      }
    }
  }

  private void removeEmitter(UUID userId, SseEmitter emitter) {
    List<SseEmitter> list = userEmitters.get(userId);
    if (list != null) {
      list.remove(emitter);
      if (list.isEmpty()) {
        userEmitters.remove(userId);
      }
    }
  }
}
