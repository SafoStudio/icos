package com.safo.icos.modules.compensation.infrastructure.repository.adapter;

import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.SalaryRevision;
import com.safo.icos.modules.compensation.domain.repository.CompensationRepository;
import com.safo.icos.modules.compensation.infrastructure.mapper.CompensationMapper;
import com.safo.icos.modules.compensation.infrastructure.repository.jpa.CompensationJpaRepository;
import com.safo.icos.modules.compensation.infrastructure.repository.jpa.SalaryRevisionJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CompensationRepositoryAdapter implements CompensationRepository {
    private final CompensationJpaRepository compensationJpaRepository;
    private final SalaryRevisionJpaRepository salaryRevisionJpaRepository;
    private final CompensationMapper mapper;

    @Override
    public CompensationRecord save(CompensationRecord record) {
        return mapper.toDomain(compensationJpaRepository.save(mapper.toEntity(record)));
    }

    @Override
    public Optional<CompensationRecord> findByUserId(UUID userId) {
        return compensationJpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public SalaryRevision saveRevision(SalaryRevision revision) {
        return mapper.toDomain(salaryRevisionJpaRepository.save(mapper.toEntity(revision)));
    }

    @Override
    public List<SalaryRevision> findRevisionsByUserId(UUID userId) {
        return salaryRevisionJpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
