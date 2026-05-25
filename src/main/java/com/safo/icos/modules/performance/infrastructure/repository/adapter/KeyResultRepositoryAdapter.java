package com.safo.icos.modules.performance.infrastructure.repository.adapter;

import com.safo.icos.modules.performance.domain.model.KeyResult;
import com.safo.icos.modules.performance.domain.repository.KeyResultRepository;
import com.safo.icos.modules.performance.infrastructure.mapper.PerformanceMapper;
import com.safo.icos.modules.performance.infrastructure.repository.jpa.KeyResultJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class KeyResultRepositoryAdapter implements KeyResultRepository {
    private final KeyResultJpaRepository jpaRepository;
    private final PerformanceMapper mapper;

    @Override
    public KeyResult save(KeyResult keyResult) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(keyResult)));
    }

    @Override
    public Optional<KeyResult> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<KeyResult> findByObjectiveId(UUID objectiveId) {
        return jpaRepository.findByObjectiveId(objectiveId).stream().map(mapper::toDomain).toList();
    }
}
