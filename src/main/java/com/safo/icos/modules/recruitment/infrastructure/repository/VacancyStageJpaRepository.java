package com.safo.icos.modules.recruitment.infrastructure.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyStageJpaRepository extends JpaRepository<VacancyStageJpaEntity, UUID> {
    List<VacancyStageJpaEntity> findByVacancyIdOrderByOrderIndexAsc(UUID vacancyId);
}
