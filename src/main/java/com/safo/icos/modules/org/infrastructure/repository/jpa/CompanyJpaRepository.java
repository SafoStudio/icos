package com.safo.icos.modules.org.infrastructure.repository.jpa;

import com.safo.icos.modules.org.infrastructure.repository.entity.CompanyJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {
}
