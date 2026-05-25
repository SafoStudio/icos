package com.safo.icos.modules.compensation.infrastructure.repository.adapter;

import com.safo.icos.modules.compensation.domain.model.Bonus;
import com.safo.icos.modules.compensation.domain.model.PayrollPeriod;
import com.safo.icos.modules.compensation.domain.repository.PayrollRepository;
import com.safo.icos.modules.compensation.infrastructure.mapper.CompensationMapper;
import com.safo.icos.modules.compensation.infrastructure.repository.jpa.BonusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PayrollRepositoryAdapter implements PayrollRepository {
    private final BonusJpaRepository bonusJpaRepository;
    private final CompensationMapper mapper;

    @Override
    public Bonus saveBonus(Bonus bonus) {
        return mapper.toDomain(bonusJpaRepository.save(mapper.toEntity(bonus)));
    }

    @Override
    public List<Bonus> findBonusesToPay(UUID userId, LocalDate startDate, LocalDate endDate) {
        return bonusJpaRepository.findByUserIdAndPaymentDateBetween(userId, startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PayrollPeriod savePeriod(PayrollPeriod period) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<PayrollPeriod> findAllPeriods() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
