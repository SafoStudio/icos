package com.safo.icos.modules.compensation.application;

import com.safo.icos.modules.compensation.application.dto.PayrollEntryDto;
import com.safo.icos.modules.compensation.domain.model.Bonus;
import com.safo.icos.modules.compensation.domain.repository.CompensationRepository;
import com.safo.icos.modules.compensation.domain.repository.PayrollRepository;
import com.safo.icos.modules.identity.user.api.UserProvider;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final CompensationRepository compensationRepository;
    private final PayrollRepository payrollRepository;
    private final UserProvider userProvider;

    public List<PayrollEntryDto> calculatePayroll(UUID companyId, LocalDate startDate, LocalDate endDate) {
        // Since UserProvider doesn't have findAllByCompanyId, this logic needs adjustment
        // For demonstration, we assume we can filter or fetch differently
        return new ArrayList<>();
    }

    @Transactional
    public Bonus awardBonus(UUID userId, BigDecimal amount, String type, String description, LocalDate paymentDate) {
        Bonus bonus = Bonus.create(userId, amount, type, description, paymentDate);
        return payrollRepository.saveBonus(bonus);
    }
}
