package com.safo.icos.modules.performance.application.listener;

import com.safo.icos.modules.notification.application.NotificationService;
import com.safo.icos.modules.notification.domain.model.NotificationType;
import com.safo.icos.modules.performance.api.event.PerformanceReviewFinalizedEvent;
import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PerformanceEventListener {

    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handlePerformanceReviewFinalized(PerformanceReviewFinalizedEvent event) {
        PerformanceReview review = event.getReview();
        log.info("Handling PerformanceReviewFinalizedEvent for employee: {}", review.getEmployeeId());

        // Notify employee
        notificationService.createNotification(
                review.getEmployeeId(),
                NotificationType.PERFORMANCE_REVIEW_FINALIZED,
                "Performance Review Finalized",
                "Your performance review has been finalized. Final Rating: " + review.getRating(),
                Map.of("reviewId", review.getId().toString())
        );

        // If rating is high (4 or 5), recommend reward to manager
        if (review.getRating() != null && review.getRating() >= 4) {
            notificationService.createNotification(
                    review.getManagerId(),
                    NotificationType.GENERAL_MESSAGE,
                    "Reward Recommendation",
                    "Employee performed exceptionally (Rating: " + review.getRating() + "). Consider a bonus or salary revision.",
                    Map.of(
                            "employeeId", review.getEmployeeId().toString(),
                            "reviewId", review.getId().toString(),
                            "recommendation", "BONUS_OR_PROMOTION"
                    )
            );
        }
    }
}
