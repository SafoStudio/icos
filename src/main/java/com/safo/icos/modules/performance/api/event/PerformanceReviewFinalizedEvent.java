package com.safo.icos.modules.performance.api.event;

import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PerformanceReviewFinalizedEvent extends ApplicationEvent {
    private final PerformanceReview review;

    public PerformanceReviewFinalizedEvent(Object source, PerformanceReview review) {
        super(source);
        this.review = review;
    }
}
