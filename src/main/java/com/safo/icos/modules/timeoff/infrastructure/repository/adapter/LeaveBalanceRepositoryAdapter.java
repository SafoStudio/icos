package com.safo.icos.modules.timeoff.infrastructure.repository.adapter;

import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import com.safo.icos.modules.timeoff.domain.repository.LeaveBalanceRepository;
import com.safo.icos.modules.timeoff.infrastructure.mapper.TimeOffMapper;
import com.safo.icos.modules.timeoff.infrastructure.repository.jpa.LeaveBalanceJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LeaveBalanceRepositoryAdapter implements LeaveBalanceRepository {
    private final LeaveBalanceJpaRepository jpaRepository;
    private final TimeOffMapper mapper;

    @Override
    public LeaveBalance save(LeaveBalance balance) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(balance)));
    }

    @Override
    public List<LeaveBalance> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<LeaveBalance> findByUserIdAndLeaveTypeId(UUID userId, UUID leaveTypeId) {
        return jpaRepository.findByUserIdAndLeaveTypeId(userId, leaveTypeId).map(mapper::toDomain);
    }
}
