package com.safo.icos.modules.compensation.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.SalaryRevision;
import com.safo.icos.modules.compensation.domain.model.enums.PayFrequency;
import com.safo.icos.modules.compensation.domain.repository.CompensationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.never;
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
    when(compensationRepository.save(any(CompensationRecord.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    CompensationRecord result = compensationService.setInitialSalary(
            USER_ID, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, LocalDate.now());

    assertThat(result.getUserId()).isEqualTo(USER_ID);
    assertThat(result.getBaseSalary()).isEqualByComparingTo("5000");
    assertThat(result.getCurrency()).isEqualTo("USD");
    assertThat(result.getPayFrequency()).isEqualTo(PayFrequency.MONTHLY);

    verify(compensationRepository).save(any(CompensationRecord.class));
    verify(compensationRepository, never()).saveRevision(any());
  }

  @Test
  void shouldThrowWhenSettingNonPositiveSalary() {
    BigDecimal nonPositiveSalary = BigDecimal.ZERO;
    String currency = "USD";
    PayFrequency frequency = PayFrequency.MONTHLY;
    LocalDate effectiveDate = LocalDate.now();

    assertThatThrownBy(() -> compensationService.setInitialSalary(
            USER_ID, nonPositiveSalary, currency, frequency, effectiveDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("positive");

    verify(compensationRepository, never()).save(any());
  }

  @Test
  void shouldThrowWhenSettingNullCurrency() {
    BigDecimal salary = BigDecimal.valueOf(5000);
    PayFrequency frequency = PayFrequency.MONTHLY;
    LocalDate effectiveDate = LocalDate.now();

    assertThatThrownBy(() -> compensationService.setInitialSalary(
            USER_ID, salary, null, frequency, effectiveDate))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Currency");

    verify(compensationRepository, never()).save(any());
  }

  @Test
  void shouldUpdateSalaryAndSaveRevision() {
    CompensationRecord current = CompensationRecord.create(
            USER_ID, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, LocalDate.now());
    LocalDate newEffectiveDate = LocalDate.of(2026, 9, 1);

    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.of(current));
    when(compensationRepository.saveRevision(any(SalaryRevision.class)))
            .thenAnswer(inv -> inv.getArgument(0));
    when(compensationRepository.save(any(CompensationRecord.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    CompensationRecord result = compensationService.updateSalary(
            USER_ID, BigDecimal.valueOf(6000), "Promotion", newEffectiveDate, APPROVER_ID);

    assertThat(result.getBaseSalary()).isEqualByComparingTo("6000");
    assertThat(result.getEffectiveDate()).isEqualTo(newEffectiveDate);

    ArgumentCaptor<SalaryRevision> captor = ArgumentCaptor.forClass(SalaryRevision.class);
    verify(compensationRepository).saveRevision(captor.capture());
    SalaryRevision revision = captor.getValue();
    assertThat(revision.getUserId()).isEqualTo(USER_ID);
    assertThat(revision.getPreviousSalary()).isEqualByComparingTo("5000");
    assertThat(revision.getNewSalary()).isEqualByComparingTo("6000");
    assertThat(revision.getReason()).isEqualTo("Promotion");
    assertThat(revision.getApprovedBy()).isEqualTo(APPROVER_ID);
    assertThat(revision.getEffectiveDate()).isEqualTo(newEffectiveDate);

    verify(compensationRepository).save(any(CompensationRecord.class));
  }

  @Test
  void shouldUseDefaultReasonWhenUpdatingWithBlankReason() {
    CompensationRecord current = CompensationRecord.create(
            USER_ID, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, LocalDate.now());

    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.of(current));
    when(compensationRepository.saveRevision(any(SalaryRevision.class)))
            .thenAnswer(inv -> inv.getArgument(0));
    when(compensationRepository.save(any(CompensationRecord.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    compensationService.updateSalary(
            USER_ID, BigDecimal.valueOf(6000), "   ", LocalDate.now(), APPROVER_ID);

    ArgumentCaptor<SalaryRevision> captor = ArgumentCaptor.forClass(SalaryRevision.class);
    verify(compensationRepository).saveRevision(captor.capture());
    assertThat(captor.getValue().getReason()).isEqualTo("Salary update");
  }

  @Test
  void shouldThrowWhenUpdatingNonExistentRecord() {
    BigDecimal newSalary = BigDecimal.valueOf(6000);
    LocalDate effectiveDate = LocalDate.now();

    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> compensationService.updateSalary(
            USER_ID, newSalary, "Raise", effectiveDate, APPROVER_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");

    verify(compensationRepository, never()).save(any());
    verify(compensationRepository, never()).saveRevision(any());
  }

  @Test
  void shouldThrowWhenUpdatingWithNonPositiveSalary() {
    BigDecimal nonPositiveSalary = BigDecimal.valueOf(-100);
    LocalDate effectiveDate = LocalDate.now();

    assertThatThrownBy(() -> compensationService.updateSalary(
            USER_ID, nonPositiveSalary, "Raise", effectiveDate, APPROVER_ID))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("positive");

    verify(compensationRepository, never()).save(any());
  }

  @Test
  void shouldCreateInitialSalaryWhenUpsertingNonExistentRecord() {
    LocalDate effectiveDate = LocalDate.of(2026, 9, 1);

    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
    when(compensationRepository.save(any(CompensationRecord.class)))
            .thenAnswer(inv -> inv.getArgument(0));
    when(compensationRepository.saveRevision(any(SalaryRevision.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    CompensationRecord result = compensationService.upsertSalary(
            USER_ID, BigDecimal.valueOf(6000), "EUR", PayFrequency.WEEKLY,
            "Raise", effectiveDate, APPROVER_ID);

    assertThat(result.getUserId()).isEqualTo(USER_ID);
    assertThat(result.getBaseSalary()).isEqualByComparingTo("6000");
    assertThat(result.getCurrency()).isEqualTo("EUR");
    assertThat(result.getPayFrequency()).isEqualTo(PayFrequency.WEEKLY);

    ArgumentCaptor<SalaryRevision> captor = ArgumentCaptor.forClass(SalaryRevision.class);
    verify(compensationRepository).saveRevision(captor.capture());
    SalaryRevision revision = captor.getValue();
    assertThat(revision.getPreviousSalary()).isEqualByComparingTo("0");
    assertThat(revision.getNewSalary()).isEqualByComparingTo("6000");
    assertThat(revision.getReason()).isEqualTo("Raise");
  }

  @Test
  void shouldUpdateExistingRecordWhenUpserting() {
    CompensationRecord current = CompensationRecord.create(
            USER_ID, BigDecimal.valueOf(5000), "USD", PayFrequency.MONTHLY, LocalDate.now());

    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.of(current));
    when(compensationRepository.save(any(CompensationRecord.class)))
            .thenAnswer(inv -> inv.getArgument(0));
    when(compensationRepository.saveRevision(any(SalaryRevision.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    CompensationRecord result = compensationService.upsertSalary(
            USER_ID, BigDecimal.valueOf(7000), "EUR", PayFrequency.WEEKLY,
            "Promotion", LocalDate.now(), APPROVER_ID);

    assertThat(result.getBaseSalary()).isEqualByComparingTo("7000");

    ArgumentCaptor<SalaryRevision> captor = ArgumentCaptor.forClass(SalaryRevision.class);
    verify(compensationRepository).saveRevision(captor.capture());
    assertThat(captor.getValue().getPreviousSalary()).isEqualByComparingTo("5000");
    assertThat(captor.getValue().getNewSalary()).isEqualByComparingTo("7000");
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

    verify(compensationRepository).findRevisionsByUserId(USER_ID);
  }

  @Test
  void shouldThrowWhenCompensationNotFound() {
    when(compensationRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> compensationService.getCompensation(USER_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");

    verify(compensationRepository, never()).save(any());
  }
}