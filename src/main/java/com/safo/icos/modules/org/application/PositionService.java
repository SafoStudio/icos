package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.PositionDto;
import com.safo.icos.modules.org.domain.model.Position;
import com.safo.icos.modules.org.domain.repository.PositionRepository;
import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.security.context.TenantContext;
import com.safo.icos.security.permission.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {
    private final PositionRepository positionRepository;

    @RequirePermission("position:manage")
    public PositionDto createPosition(String name, String description) {
        UUID companyId = UUID.fromString(TenantContext.getTenantId());
        
        if (positionRepository.existsByCompanyIdAndName(companyId, name)) {
            throw new BusinessException("Position already exists", ErrorCode.POSITION_ALREADY_EXISTS);
        }
        
        Position position = Position.create(companyId, name, description);
        Position saved = positionRepository.save(position);
        return toDto(saved);
    }

    @RequirePermission("position:manage")
    public PositionDto updatePosition(UUID id, String name, String description) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Position not found", ErrorCode.POSITION_NOT_FOUND));
        
        position.update(name, description);
        Position saved = positionRepository.save(position);
        return toDto(saved);
    }

    public List<PositionDto> getPositionsByCompany(UUID companyId) {
        List<Position> positions = positionRepository.findByCompanyId(companyId);
        if (positions == null) {
            return Collections.emptyList();
        }
        return positions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private PositionDto toDto(Position domain) {
        return PositionDto.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .description(domain.getDescription())
                .build();
    }
}
