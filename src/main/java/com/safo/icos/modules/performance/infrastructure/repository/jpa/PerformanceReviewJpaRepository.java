package com.safo.icos.modules.performance.infrastructure.repository.jpa;

import com.safo.icos.modules.performance.infrastructure.repository.entity.PerformanceReviewJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceReviewJpaRepository extends JpaRepository<PerformanceReviewJpaEntity, UUID> {
    List<PerformanceReviewJpaEntity> findByEmployeeId(UUID employeeId);
    List<PerformanceReviewJpaEntity> findByCycleId(UUID cycleId);
}
