package com.safo.icos.modules.compensation.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.SalaryRevision;
import com.safo.icos.modules.compensation.domain.model.enums.PayFrequency;
import com.safo.icos.modules.compensation.domain.repository.CompensationRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompensationServiceTest {

  @Mock private CompensationRepository compensationRepository;

  @InjectMocks
  private CompensationService compensationService;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID APPROVER_ID = UUID.randomUUID();

  @Test
  void shouldSetInitialSalary() {
    when(compensationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    CompensationRecord result = compensationService.setInitialSalary(
            USER_ID, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, LocalDate.now());

    assertThat(result.getBaseSalary()).isEqualByComparingTo("5000");
    assertThat(result.getCurrency()).isEqualTo("USD");
    verify(compensationRepository).save(any());
  }

  @Test
  void shouldUpdateSalaryAndSaveRevision() {
    CompensationRecord current = CompensationRecord.create(
            USER_ID, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, LocalDate.now());
    LocalDate newEffectiveDate = LocalDate.of(2026, 9, 1);

    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.of(current));
    when(compensationRepository.saveRevision(any())).thenAnswer(inv -> inv.getArgument(0));
    when(compensationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    CompensationRecord result = compensationService.updateSalary(
            USER_ID, BigDecimal.valueOf(6000), "Promotion", newEffectiveDate, APPROVER_ID);

    assertThat(result.getBaseSalary()).isEqualByComparingTo("6000");
    verify(compensationRepository).saveRevision(any());
  }

  @Test
  void shouldThrowWhenUpdatingNonExistentRecord() {
    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> compensationService.updateSalary(
            USER_ID, BigDecimal.valueOf(6000), "Raise", LocalDate.now(), APPROVER_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldGetSalaryHistory() {
    SalaryRevision rev1 = SalaryRevision.create(USER_ID, BigDecimal.valueOf(4000), BigDecimal.valueOf(5000),
            "Initial", LocalDate.now(), APPROVER_ID);
    SalaryRevision rev2 = SalaryRevision.create(USER_ID, BigDecimal.valueOf(5000), BigDecimal.valueOf(6000),
            "Promotion", LocalDate.now(), APPROVER_ID);

    when(compensationRepository.findRevisionsByUserId(USER_ID)).thenReturn(List.of(rev1, rev2));

    List<SalaryRevision> history = compensationService.getSalaryHistory(USER_ID);

    assertThat(history).hasSize(2);
  }

  @Test
  void shouldThrowWhenCompensationNotFound() {
    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> compensationService.getCompensation(USER_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
  }
}