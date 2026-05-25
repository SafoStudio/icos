package com.safo.icos.modules.timeoff.infrastructure.repository.adapter;

import com.safo.icos.modules.timeoff.domain.model.LeaveType;
import com.safo.icos.modules.timeoff.domain.repository.LeaveTypeRepository;
import com.safo.icos.modules.timeoff.infrastructure.mapper.TimeOffMapper;
import com.safo.icos.modules.timeoff.infrastructure.repository.jpa.LeaveTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LeaveTypeRepositoryAdapter implements LeaveTypeRepository {
    private final LeaveTypeJpaRepository jpaRepository;
    private final TimeOffMapper mapper;

    @Override
    public List<LeaveType> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<LeaveType> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<LeaveType> findByCode(String code) {
        return jpaRepository.findByCode(code).map(mapper::toDomain);
    }
}
