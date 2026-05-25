package com.safo.icos.modules.timeoff.infrastructure.repository.adapter;

import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import com.safo.icos.modules.timeoff.domain.repository.LeaveRequestRepository;
import com.safo.icos.modules.timeoff.infrastructure.mapper.TimeOffMapper;
import com.safo.icos.modules.timeoff.infrastructure.repository.jpa.LeaveRequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LeaveRequestRepositoryAdapter implements LeaveRequestRepository {
    private final LeaveRequestJpaRepository jpaRepository;
    private final TimeOffMapper mapper;

    @Override
    public LeaveRequest save(LeaveRequest request) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(request)));
    }

    @Override
    public Optional<LeaveRequest> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<LeaveRequest> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<LeaveRequest> findByManagerId(UUID managerId) {
        // Требует реализации query в JpaRepository
        return List.of();
    }
}
