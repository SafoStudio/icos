package com.safo.icos.modules.compensation.infrastructure.repository.jpa;

import com.safo.icos.modules.compensation.infrastructure.repository.entity.SalaryRevisionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SalaryRevisionJpaRepository extends JpaRepository<SalaryRevisionJpaEntity, UUID> {
    List<SalaryRevisionJpaEntity> findByUserId(UUID userId);
}
