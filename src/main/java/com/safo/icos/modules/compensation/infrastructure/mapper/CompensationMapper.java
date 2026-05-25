package com.safo.icos.modules.compensation.infrastructure.mapper;

import com.safo.icos.modules.compensation.api.dto.CompensationDto;
import com.safo.icos.modules.compensation.api.dto.SalaryRevisionDto;
import com.safo.icos.modules.compensation.domain.model.*;
import com.safo.icos.modules.compensation.domain.model.enums.PayFrequency;
import com.safo.icos.modules.compensation.infrastructure.repository.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CompensationMapper {

    public CompensationRecord toDomain(CompensationRecordJpaEntity entity) {
        if (entity == null) return null;
        return CompensationRecord.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .baseSalary(entity.getBaseSalary())
                .currency(entity.getCurrency())
                .payFrequency(PayFrequency.valueOf(entity.getPayFrequency()))
                .effectiveDate(entity.getEffectiveDate())
                .build();
    }

    public CompensationRecordJpaEntity toEntity(CompensationRecord domain) {
        if (domain == null) return null;
        return CompensationRecordJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .baseSalary(domain.getBaseSalary())
                .currency(domain.getCurrency())
                .payFrequency(domain.getPayFrequency().name())
                .effectiveDate(domain.getEffectiveDate())
                .build();
    }

    public SalaryRevision toDomain(SalaryRevisionJpaEntity entity) {
        if (entity == null) return null;
        return SalaryRevision.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .previousSalary(entity.getPreviousSalary())
                .newSalary(entity.getNewSalary())
                .reason(entity.getReason())
                .effectiveDate(entity.getEffectiveDate())
                .approvedBy(entity.getApprovedBy())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SalaryRevisionJpaEntity toEntity(SalaryRevision domain) {
        if (domain == null) return null;
        return SalaryRevisionJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .previousSalary(domain.getPreviousSalary())
                .newSalary(domain.getNewSalary())
                .reason(domain.getReason())
                .effectiveDate(domain.getEffectiveDate())
                .approvedBy(domain.getApprovedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public CompensationDto toDto(CompensationRecord domain) {
        if (domain == null) return null;
        return CompensationDto.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .baseSalary(domain.getBaseSalary())
                .currency(domain.getCurrency())
                .payFrequency(domain.getPayFrequency().name())
                .effectiveDate(domain.getEffectiveDate())
                .build();
    }

    public SalaryRevisionDto toDto(SalaryRevision domain) {
        if (domain == null) return null;
        return SalaryRevisionDto.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .previousSalary(domain.getPreviousSalary())
                .newSalary(domain.getNewSalary())
                .reason(domain.getReason())
                .effectiveDate(domain.getEffectiveDate())
                .build();
    }

    public Bonus toDomain(BonusJpaEntity entity) {
        if (entity == null) return null;
        return Bonus.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .type(entity.getType())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .paymentDate(entity.getPaymentDate())
                .build();
    }

    public BonusJpaEntity toEntity(Bonus domain) {
        if (domain == null) return null;
        return BonusJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .amount(domain.getAmount())
                .type(domain.getType())
                .description(domain.getDescription())
                .status(domain.getStatus())
                .paymentDate(domain.getPaymentDate())
                .build();
    }
}
