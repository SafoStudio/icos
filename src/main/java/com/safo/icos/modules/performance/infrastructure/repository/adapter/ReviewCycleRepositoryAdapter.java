package com.safo.icos.modules.performance.infrastructure.repository.adapter;

import com.safo.icos.modules.performance.domain.model.ReviewCycle;
import com.safo.icos.modules.performance.domain.repository.ReviewCycleRepository;
import com.safo.icos.modules.performance.infrastructure.mapper.PerformanceMapper;
import com.safo.icos.modules.performance.infrastructure.repository.jpa.ReviewCycleJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewCycleRepositoryAdapter implements ReviewCycleRepository {
    private final ReviewCycleJpaRepository jpaRepository;
    private final PerformanceMapper mapper;

    @Override
    public ReviewCycle save(ReviewCycle cycle) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(cycle)));
    }

    @Override
    public Optional<ReviewCycle> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ReviewCycle> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}
