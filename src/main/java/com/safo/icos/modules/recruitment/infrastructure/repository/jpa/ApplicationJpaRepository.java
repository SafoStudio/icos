package com.safo.icos.modules.recruitment.infrastructure.repository.jpa;

import com.safo.icos.modules.recruitment.infrastructure.repository.entity.ApplicationJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationJpaRepository extends JpaRepository<ApplicationJpaEntity, UUID> {
    List<ApplicationJpaEntity> findByVacancyId(UUID vacancyId);
    List<ApplicationJpaEntity> findByCandidateId(UUID candidateId);
}
