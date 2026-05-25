package com.safo.icos.modules.timeoff.domain.repository;

import com.safo.icos.modules.timeoff.domain.model.LeaveType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveTypeRepository {
    List<LeaveType> findAllActive();
    Optional<LeaveType> findById(UUID id);
    Optional<LeaveType> findByCode(String code);
}
