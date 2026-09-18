package com.safo.icos.modules.performance.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.performance.api.event.PerformanceReviewFinalizedEvent;
import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import com.safo.icos.modules.performance.domain.model.ReviewCycle;
import com.safo.icos.modules.performance.domain.repository.PerformanceReviewRepository;
import com.safo.icos.modules.performance.domain.repository.ReviewCycleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerformanceService {
  private final ReviewCycleRepository cycleRepository;
  private final PerformanceReviewRepository reviewRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public ReviewCycle createCycle(String name, LocalDate start, LocalDate end) {
    ReviewCycle cycle = ReviewCycle.create(name, start, end);
    return cycleRepository.save(cycle);
  }

  @Transactional
  public PerformanceReview createReview(UUID cycleId, UUID employeeId, UUID managerId) {
    PerformanceReview review = PerformanceReview.create(cycleId, employeeId, managerId);
    return reviewRepository.save(review);
  }

  @Transactional
  public PerformanceReview finalizeReview(UUID reviewId, Integer rating, String summary) {
    PerformanceReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new NotFoundException("Review not found"));
    PerformanceReview finalized = review.finalize(rating, summary);
    PerformanceReview saved = reviewRepository.save(finalized);
    eventPublisher.publishEvent(new PerformanceReviewFinalizedEvent(this, saved));
    return saved;
  }

  @Transactional
  public PerformanceReview submitReview(UUID employeeId, UUID managerId, Integer rating, String summary) {
    ReviewCycle cycle = cycleRepository.findAll().stream().findFirst().orElseGet(() ->
        cycleRepository.save(ReviewCycle.create("Annual Review " + LocalDate.now().getYear(), LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6)))
    );
    PerformanceReview review = PerformanceReview.create(cycle.getId(), employeeId, managerId);
    PerformanceReview finalized = review.finalize(rating, summary);
    PerformanceReview saved = reviewRepository.save(finalized);
    eventPublisher.publishEvent(new PerformanceReviewFinalizedEvent(this, saved));
    return saved;
  }

  public List<PerformanceReview> getEmployeeReviews(UUID employeeId) {
    return reviewRepository.findByEmployeeId(employeeId);
  }
}
