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
  private final CompensationRepository compensationRepository;

  @Transactional
  public CompensationRecord setInitialSalary(UUID userId, BigDecimal salary, String currency,
                                             PayFrequency frequency, LocalDate effectiveDate) {
    CompensationRecord compensationRecord = CompensationRecord.create(userId, salary, currency, frequency, effectiveDate);
    return compensationRepository.save(compensationRecord);
  }

  @Transactional
  public CompensationRecord updateSalary(UUID userId, BigDecimal newSalary, String reason,
                                         LocalDate effectiveDate, UUID approvedBy) {
    CompensationRecord current = compensationRepository.findByUserId(userId).orElse(null);
    if (current == null) {
      CompensationRecord initial = CompensationRecord.create(userId, newSalary, "USD", PayFrequency.MONTHLY, effectiveDate);
      CompensationRecord saved = compensationRepository.save(initial);
      SalaryRevision revision = SalaryRevision.create(userId, BigDecimal.ZERO, newSalary,
              reason != null && !reason.isBlank() ? reason : "Initial salary", effectiveDate, approvedBy);
      compensationRepository.saveRevision(revision);
      return saved;
    }
    SalaryRevision revision = SalaryRevision.create(userId, current.getBaseSalary(), newSalary,
            reason, effectiveDate, approvedBy);
    compensationRepository.saveRevision(revision);
    CompensationRecord updated = current.toBuilder().baseSalary(newSalary).effectiveDate(effectiveDate).build();
    return compensationRepository.save(updated);
  }

  public List<SalaryRevision> getSalaryHistory(UUID userId) {
    return compensationRepository.findRevisionsByUserId(userId);
  }

  public CompensationRecord getCompensation(UUID userId) {
    return compensationRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Compensation record not found"));
  }
}