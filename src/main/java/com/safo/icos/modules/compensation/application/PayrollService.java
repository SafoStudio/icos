package com.safo.icos.modules.compensation.application;

import com.safo.icos.modules.compensation.application.dto.PayrollEntryDto;
import com.safo.icos.modules.compensation.domain.model.Bonus;
import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.PayrollPeriod;
import com.safo.icos.modules.compensation.domain.repository.CompensationRepository;
import com.safo.icos.modules.compensation.domain.repository.PayrollRepository;
import com.safo.icos.modules.user.api.UserProvider;
import com.safo.icos.modules.user.api.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final CompensationRepository compensationRepository;
    private final PayrollRepository payrollRepository;
    // We'll use this to get all employees eventually
    // For now, let's assume we can fetch them via UserProvider

    public List<PayrollEntryDto> calculatePayroll(UUID periodId) {
        // This is a simplified calculation
        // In real life, it would iterate over employees
        return new ArrayList<>();
    }

    @Transactional
    public Bonus awardBonus(UUID userId, BigDecimal amount, String type, String description, LocalDate paymentDate) {
        Bonus bonus = Bonus.create(userId, amount, type, description, paymentDate);
        return payrollRepository.saveBonus(bonus);
    }
}
