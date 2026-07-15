package com.safo.icos.modules.compensation.domain.repository;

import com.safo.icos.modules.compensation.domain.model.Bonus;
import com.safo.icos.modules.compensation.domain.model.PayrollPeriod;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PayrollRepository {
    Bonus saveBonus(Bonus bonus);
    List<Bonus> findBonusesToPay(UUID userId, LocalDate startDate, LocalDate endDate);

    PayrollPeriod savePeriod(PayrollPeriod period);
    List<PayrollPeriod> findAllPeriods();
}
