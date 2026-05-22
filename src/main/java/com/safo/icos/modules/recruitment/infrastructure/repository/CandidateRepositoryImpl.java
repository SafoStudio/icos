package com.safo.icos.modules.recruitment.infrastructure.repository;

import com.safo.icos.modules.recruitment.domain.model.Candidate;
import com.safo.icos.modules.recruitment.domain.repository.CandidateRepository;
import com.safo.icos.modules.recruitment.infrastructure.mapper.RecruitmentMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CandidateRepositoryImpl implements CandidateRepository {
    private final CandidateJpaRepository jpaRepository;
    private final RecruitmentMapper mapper;

    @Override
    public Candidate save(Candidate candidate) {
        CandidateJpaEntity entity = mapper.toEntity(candidate);
        CandidateJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Candidate> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
