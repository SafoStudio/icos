package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.PositionDto;
import com.safo.icos.modules.org.domain.model.Position;
import com.safo.icos.modules.org.domain.repository.PositionRepository;
import com.safo.icos.security.permission.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {
    private final PositionRepository positionRepository;

    @RequirePermission("pos:manage")
    public PositionDto createPosition(UUID companyId, String name, String description) {
        Position position = Position.create(companyId, name, description);
        Position saved = positionRepository.save(position);
        return toDto(saved);
    }

    public List<PositionDto> getPositionsByCompany(UUID companyId) {
        return positionRepository.findByCompanyId(companyId).stream()
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