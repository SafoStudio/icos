package com.safo.icos.modules.talent.recruitment.infrastructure.repository.adapter;

import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;
import com.safo.icos.modules.talent.recruitment.domain.repository.CandidateRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity.CandidateJpaEntity;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.CandidateJpaRepository;
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
