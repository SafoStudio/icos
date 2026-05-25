package com.safo.icos.modules.timeoff.infrastructure.repository.adapter;

import com.safo.icos.modules.timeoff.domain.model.LeaveAuditLog;
import com.safo.icos.modules.timeoff.domain.repository.LeaveAuditLogRepository;
import com.safo.icos.modules.timeoff.infrastructure.mapper.TimeOffMapper;
import com.safo.icos.modules.timeoff.infrastructure.repository.jpa.LeaveAuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LeaveAuditLogRepositoryAdapter implements LeaveAuditLogRepository {
    private final LeaveAuditLogJpaRepository jpaRepository;
    private final TimeOffMapper mapper;

    @Override
    public LeaveAuditLog save(LeaveAuditLog log) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(log)));
    }

    @Override
    public List<LeaveAuditLog> findByLeaveRequestId(UUID leaveRequestId) {
        return jpaRepository.findByLeaveRequestId(leaveRequestId).stream().map(mapper::toDomain).toList();
    }
}
