package com.safo.icos.modules.timeoff.infrastructure.repository.jpa;

import com.safo.icos.modules.timeoff.infrastructure.repository.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LeaveRequestJpaRepository extends JpaRepository<LeaveRequestJpaEntity, UUID> {
    List<LeaveRequestJpaEntity> findByUserId(UUID userId);
}
