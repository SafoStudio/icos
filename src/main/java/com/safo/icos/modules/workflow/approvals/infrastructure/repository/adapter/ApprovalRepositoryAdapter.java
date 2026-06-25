package com.safo.icos.modules.workflow.approvals.infrastructure.repository.adapter;

import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import com.safo.icos.modules.workflow.approvals.domain.repository.ApprovalRepository;
import com.safo.icos.modules.workflow.approvals.infrastructure.mapper.ApprovalMapper;
import com.safo.icos.modules.workflow.approvals.infrastructure.repository.jpa.ApprovalJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApprovalRepositoryAdapter implements ApprovalRepository {
    private final ApprovalJpaRepository jpaRepository;
    private final ApprovalMapper mapper;

    @Override
    public ApprovalRequest save(ApprovalRequest request) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(request)));
    }

    @Override
    public Optional<ApprovalRequest> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<ApprovalRequest> findAllByApproverId(UUID approverId) {
        return jpaRepository.findAllByApproverId(approverId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
