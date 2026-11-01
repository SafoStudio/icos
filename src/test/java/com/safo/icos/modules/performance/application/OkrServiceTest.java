package com.safo.icos.modules.performance.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.performance.domain.model.KeyResult;
import com.safo.icos.modules.performance.domain.model.Objective;
import com.safo.icos.modules.performance.domain.repository.KeyResultRepository;
import com.safo.icos.modules.performance.domain.repository.ObjectiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OkrServiceTest {

  @Mock private ObjectiveRepository objectiveRepository;
  @Mock private KeyResultRepository keyResultRepository;

  @InjectMocks
  private OkrService okrService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID OBJECTIVE_ID = UUID.randomUUID();
  private static final UUID KR_ID = UUID.randomUUID();

  @Test
  void shouldCreateObjective() {
    when(objectiveRepository.save(any(Objective.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    Objective result = okrService.createObjective(USER_ID, "Grow revenue", "Increase by 20%",
            LocalDate.now(), LocalDate.now().plusMonths(3));

    assertThat(result.getTitle()).isEqualTo("Grow revenue");
    assertThat(result.getProgress()).isEqualByComparingTo("0");

    verify(objectiveRepository).save(any(Objective.class));
  }

  @Test
  void shouldAddKeyResultAndRecalculateProgress() {
    Objective objective = Objective.create(USER_ID, "Goal", "Desc",
            LocalDate.now(), LocalDate.now().plusMonths(3));
    KeyResult kr = KeyResult.create(OBJECTIVE_ID, "KR1",
            BigDecimal.valueOf(100), "%", BigDecimal.ONE);

    when(objectiveRepository.findById(OBJECTIVE_ID)).thenReturn(Optional.of(objective));
    when(keyResultRepository.save(any(KeyResult.class))).thenReturn(kr);
    when(keyResultRepository.findByObjectiveId(OBJECTIVE_ID)).thenReturn(List.of(kr));
    when(objectiveRepository.save(any(Objective.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    KeyResult result = okrService.addKeyResult(
            OBJECTIVE_ID, "KR1", BigDecimal.valueOf(100), "%", BigDecimal.ONE);

    assertThat(result.getTitle()).isEqualTo("KR1");
    assertThat(result.getObjectiveId()).isEqualTo(OBJECTIVE_ID);

    verify(objectiveRepository, times(2)).findById(OBJECTIVE_ID);
    verify(keyResultRepository).save(any(KeyResult.class));
    verify(keyResultRepository).findByObjectiveId(OBJECTIVE_ID);
    verify(objectiveRepository).save(any(Objective.class));
  }

  @Test
  void shouldThrowWhenAddingKeyResultToNonExistentObjective() {
    when(objectiveRepository.findById(OBJECTIVE_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> okrService.addKeyResult(
            OBJECTIVE_ID, "KR", BigDecimal.ONE, "%", BigDecimal.ONE))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");

    verify(keyResultRepository, times(0)).save(any());
  }

  @Test
  void shouldUpdateKeyResultProgress() {
    KeyResult kr = KeyResult.builder()
            .id(KR_ID).objectiveId(OBJECTIVE_ID).title("KR1")
            .targetValue(BigDecimal.valueOf(100)).currentValue(BigDecimal.ZERO)
            .unit("%").weight(BigDecimal.ONE).build();
    Objective objective = Objective.create(USER_ID, "Goal", "Desc",
            LocalDate.now(), LocalDate.now().plusMonths(3));

    when(keyResultRepository.findById(KR_ID)).thenReturn(Optional.of(kr));
    when(keyResultRepository.save(any(KeyResult.class)))
            .thenAnswer(inv -> inv.getArgument(0));
    when(objectiveRepository.findById(OBJECTIVE_ID)).thenReturn(Optional.of(objective));
    when(keyResultRepository.findByObjectiveId(OBJECTIVE_ID)).thenReturn(List.of(kr));
    when(objectiveRepository.save(any(Objective.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    KeyResult result = okrService.updateKeyResultProgress(KR_ID, BigDecimal.valueOf(50));

    assertThat(result.getCurrentValue()).isEqualByComparingTo("50");

    verify(keyResultRepository).findById(KR_ID);
    verify(keyResultRepository).save(any(KeyResult.class));
    verify(objectiveRepository).findById(OBJECTIVE_ID);
  }

  @Test
  void shouldThrowWhenUpdatingNonExistentKeyResult() {
    when(keyResultRepository.findById(KR_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> okrService.updateKeyResultProgress(KR_ID, BigDecimal.valueOf(50)))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");

    verify(keyResultRepository, times(0)).save(any());
  }

  @Test
  void shouldReturnUserObjectives() {
    Objective obj1 = Objective.create(USER_ID, "Goal 1", "Desc",
            LocalDate.now(), LocalDate.now().plusMonths(3));
    Objective obj2 = Objective.create(USER_ID, "Goal 2", "Desc",
            LocalDate.now(), LocalDate.now().plusMonths(6));

    when(objectiveRepository.findByUserId(USER_ID)).thenReturn(List.of(obj1, obj2));

    List<Objective> result = okrService.getUserObjectives(USER_ID);

    assertThat(result).hasSize(2);

    verify(objectiveRepository).findByUserId(USER_ID);
  }
}