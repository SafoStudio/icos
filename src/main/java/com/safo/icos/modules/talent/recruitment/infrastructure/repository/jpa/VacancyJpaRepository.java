package com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity.VacancyJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyJpaRepository extends JpaRepository<VacancyJpaEntity, UUID> {
    List<VacancyJpaEntity> findByCompanyId(UUID companyId);
}
