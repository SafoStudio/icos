package com.safo.icos.modules.performance.domain.repository;

import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerformanceReviewRepository {
    PerformanceReview save(PerformanceReview review);
    Optional<PerformanceReview> findById(UUID id);
    List<PerformanceReview> findByEmployeeId(UUID employeeId);
    List<PerformanceReview> findByCycleId(UUID cycleId);
}
