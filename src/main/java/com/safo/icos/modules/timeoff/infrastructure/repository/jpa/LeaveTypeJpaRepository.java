package com.safo.icos.modules.timeoff.infrastructure.repository.jpa;

import com.safo.icos.modules.timeoff.infrastructure.repository.entity.LeaveTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveTypeJpaRepository extends JpaRepository<LeaveTypeJpaEntity, UUID> {
    List<LeaveTypeJpaEntity> findByIsActiveTrue();
    Optional<LeaveTypeJpaEntity> findByCode(String code);
}
