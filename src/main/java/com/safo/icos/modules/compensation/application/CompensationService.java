package com.safo.icos.modules.compensation.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.SalaryRevision;
import com.safo.icos.modules.compensation.domain.model.enums.PayFrequency;
import com.safo.icos.modules.compensation.domain.repository.CompensationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompensationService {

  private static final String INITIAL_SALARY_REASON = "Initial salary";
  private static final String DEFAULT_UPDATE_REASON = "Salary update";

  private final CompensationRepository compensationRepository;

  @Transactional
  public CompensationRecord setInitialSalary(UUID userId, BigDecimal salary, String currency,
                                             PayFrequency frequency, LocalDate effectiveDate) {
    validateSalary(salary);
    validateCurrency(currency);
    validateFrequency(frequency);
    validateEffectiveDate(effectiveDate);

    CompensationRecord compensationRecord = CompensationRecord.create(
            userId, salary, currency, frequency, effectiveDate);
    return compensationRepository.save(compensationRecord);
  }

  @Transactional
  public CompensationRecord updateSalary(UUID userId, BigDecimal newSalary, String reason,
                                         LocalDate effectiveDate, UUID approvedBy) {
    validateSalary(newSalary);
    validateEffectiveDate(effectiveDate);

    CompensationRecord current = compensationRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(
                    "Compensation record not found for user " + userId));

    String normalizedReason = normalizeReason(reason, DEFAULT_UPDATE_REASON);

    SalaryRevision revision = SalaryRevision.create(
            userId, current.getBaseSalary(), newSalary, normalizedReason, effectiveDate, approvedBy);
    compensationRepository.saveRevision(revision);

    CompensationRecord updated = current.toBuilder()
            .baseSalary(newSalary)
            .effectiveDate(effectiveDate)
            .build();
    return compensationRepository.save(updated);
  }

  @Transactional
  public CompensationRecord upsertSalary(UUID userId, BigDecimal newSalary, String currency,
                                         PayFrequency frequency, String reason,
                                         LocalDate effectiveDate, UUID approvedBy) {
    validateSalary(newSalary);
    validateCurrency(currency);
    validateFrequency(frequency);
    validateEffectiveDate(effectiveDate);

    return compensationRepository.findByUserId(userId)
            .map(current -> applyUpdate(current, newSalary, reason, effectiveDate, approvedBy))
            .orElseGet(() -> createInitial(userId, newSalary, currency, frequency,
                    reason, effectiveDate, approvedBy));
  }

  public List<SalaryRevision> getSalaryHistory(UUID userId) {
    return compensationRepository.findRevisionsByUserId(userId);
  }

  public CompensationRecord getCompensation(UUID userId) {
    return compensationRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException(
                    "Compensation record not found for user " + userId));
  }

  private CompensationRecord applyUpdate(CompensationRecord current, BigDecimal newSalary,
                                         String reason, LocalDate effectiveDate, UUID approvedBy) {
    String normalizedReason = normalizeReason(reason, DEFAULT_UPDATE_REASON);

    SalaryRevision revision = SalaryRevision.create(
            current.getUserId(), current.getBaseSalary(), newSalary,
            normalizedReason, effectiveDate, approvedBy);
    compensationRepository.saveRevision(revision);

    CompensationRecord updated = current.toBuilder()
            .baseSalary(newSalary)
            .effectiveDate(effectiveDate)
            .build();
    return compensationRepository.save(updated);
  }

  private CompensationRecord createInitial(UUID userId, BigDecimal newSalary, String currency,
                                           PayFrequency frequency, String reason,
                                           LocalDate effectiveDate, UUID approvedBy) {
    CompensationRecord initial = CompensationRecord.create(
            userId, newSalary, currency, frequency, effectiveDate);
    CompensationRecord saved = compensationRepository.save(initial);

    String normalizedReason = normalizeReason(reason, INITIAL_SALARY_REASON);
    SalaryRevision revision = SalaryRevision.create(
            userId, BigDecimal.ZERO, newSalary, normalizedReason, effectiveDate, approvedBy);
    compensationRepository.saveRevision(revision);

    return saved;
  }

  private String normalizeReason(String reason, String defaultReason) {
    return (reason != null && !reason.isBlank()) ? reason : defaultReason;
  }

  private void validateSalary(BigDecimal salary) {
    if (salary == null || salary.signum() <= 0) {
      throw new IllegalArgumentException("Salary must be positive");
    }
  }

  private void validateCurrency(String currency) {
    if (currency == null || currency.isBlank()) {
      throw new IllegalArgumentException("Currency must be provided");
    }
  }

  private void validateFrequency(PayFrequency frequency) {
    if (frequency == null) {
      throw new IllegalArgumentException("Pay frequency must be provided");
    }
  }

  private void validateEffectiveDate(LocalDate effectiveDate) {
    if (effectiveDate == null) {
      throw new IllegalArgumentException("Effective date must be provided");
    }
  }
}