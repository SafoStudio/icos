package com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity.CandidateJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateJpaRepository extends JpaRepository<CandidateJpaEntity, UUID> {
}
