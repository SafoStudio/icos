package com.safo.icos.modules.performance.infrastructure.repository.adapter;

import com.safo.icos.modules.performance.domain.model.Objective;
import com.safo.icos.modules.performance.domain.repository.ObjectiveRepository;
import com.safo.icos.modules.performance.infrastructure.mapper.PerformanceMapper;
import com.safo.icos.modules.performance.infrastructure.repository.jpa.ObjectiveJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ObjectiveRepositoryAdapter implements ObjectiveRepository {
    private final ObjectiveJpaRepository jpaRepository;
    private final PerformanceMapper mapper;

    @Override
    public Objective save(Objective objective) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(objective)));
    }

    @Override
    public Optional<Objective> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Objective> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
