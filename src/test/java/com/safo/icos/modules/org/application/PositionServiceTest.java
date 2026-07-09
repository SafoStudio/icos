package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.org.application.dto.PositionDto;
import com.safo.icos.modules.org.domain.model.Position;
import com.safo.icos.modules.org.domain.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

  @Mock private PositionRepository positionRepository;

  @InjectMocks
  private PositionService positionService;

  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID POSITION_ID = UUID.randomUUID();

  @Test
  void shouldReturnPositionsByCompany() {
    Position pos = Position.create(COMPANY_ID, "Developer", "Dev");
    when(positionRepository.findByCompanyId(COMPANY_ID)).thenReturn(List.of(pos));

    List<PositionDto> result = positionService.getPositionsByCompany(COMPANY_ID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("Developer");
  }

  @Test
  void shouldReturnEmptyListWhenNoPositions() {
    when(positionRepository.findByCompanyId(COMPANY_ID)).thenReturn(null);

    List<PositionDto> result = positionService.getPositionsByCompany(COMPANY_ID);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldUpdatePosition() {
    Position pos = Position.create(COMPANY_ID, "Developer", "Old");
    when(positionRepository.findById(POSITION_ID)).thenReturn(Optional.of(pos));
    when(positionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PositionDto result = positionService.updatePosition(POSITION_ID, "Senior Dev", "Updated");

    assertThat(result.getName()).isEqualTo("Senior Dev");
    assertThat(result.getDescription()).isEqualTo("Updated");
    verify(positionRepository).save(any());
  }

  @Test
  void shouldThrowWhenUpdatePositionNotFound() {
    when(positionRepository.findById(POSITION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> positionService.updatePosition(POSITION_ID, "Dev", "New"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }
}