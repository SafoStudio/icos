package com.safo.icos.modules.performance.infrastructure.repository.adapter;

import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import com.safo.icos.modules.performance.domain.repository.PerformanceReviewRepository;
import com.safo.icos.modules.performance.infrastructure.mapper.PerformanceMapper;
import com.safo.icos.modules.performance.infrastructure.repository.jpa.PerformanceReviewJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PerformanceReviewRepositoryAdapter implements PerformanceReviewRepository {
    private final PerformanceReviewJpaRepository jpaRepository;
    private final PerformanceMapper mapper;

    @Override
    public PerformanceReview save(PerformanceReview review) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(review)));
    }

    @Override
    public Optional<PerformanceReview> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<PerformanceReview> findByEmployeeId(UUID employeeId) {
        return jpaRepository.findByEmployeeId(employeeId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PerformanceReview> findByCycleId(UUID cycleId) {
        return jpaRepository.findByCycleId(cycleId).stream().map(mapper::toDomain).toList();
    }
}
