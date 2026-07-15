package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.org.api.constant.OrgErrorCode;
import com.safo.icos.modules.org.application.dto.PositionDto;
import com.safo.icos.modules.org.domain.model.Position;
import com.safo.icos.modules.org.domain.repository.PositionRepository;
import com.safo.icos.security.context.TenantContext;
import com.safo.icos.security.permission.RequirePermission;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for position management.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PositionService {
  private final PositionRepository positionRepository;

  /**
   * Creates a new position in the current tenant's company.
   * Requires {@code position:manage} permission.
   *
   * @param name        position name
   * @param description position description
   * @return the created position
   * @throws BusinessException if a position with this name already exists
   */
  @RequirePermission("position:manage")
  public PositionDto createPosition(String name, String description) {
    UUID companyId = UUID.fromString(TenantContext.getTenantId());
    if (positionRepository.existsByCompanyIdAndName(companyId, name)) {
      throw new BusinessException("Position already exists", OrgErrorCode.POSITION_ALREADY_EXISTS);
    }
    Position position = Position.create(companyId, name, description);
    Position saved = positionRepository.save(position);
    return toDto(saved);
  }

  /**
   * Updates an existing position. Requires {@code position:manage} permission.
   *
   * @param id          position UUID
   * @param name        new name
   * @param description new description
   * @return the updated position
   * @throws BusinessException if the position is not found
   */
  @RequirePermission("position:manage")
  public PositionDto updatePosition(UUID id, String name, String description) {
    Position position = positionRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Position not found", OrgErrorCode.POSITION_NOT_FOUND));
    position.update(name, description);
    Position saved = positionRepository.save(position);
    return toDto(saved);
  }

  /**
   * Returns all positions in the given company.
   *
   * @param companyId company UUID
   * @return list of positions, never {@code null}
   */
  public List<PositionDto> getPositionsByCompany(UUID companyId) {
    List<Position> positions = positionRepository.findByCompanyId(companyId);
    if (positions == null) {
      return Collections.emptyList();
    }
    return positions.stream()
            .map(this::toDto)
            .toList();
  }

  /**
   * Deletes an existing position. Requires {@code position:manage} permission.
   *
   * @param id position UUID
   * @return true if deleted, false if not found
   */
  @RequirePermission("position:manage")
  public boolean deletePosition(UUID id) {
    if (positionRepository.findById(id).isPresent()) {
      positionRepository.deleteById(id);
      return true;
    }
    return false;
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
