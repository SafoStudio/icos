package com.safo.icos.modules.performance.application;

import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.performance.domain.model.KeyResult;
import com.safo.icos.modules.performance.domain.model.Objective;
import com.safo.icos.modules.performance.domain.repository.KeyResultRepository;
import com.safo.icos.modules.performance.domain.repository.ObjectiveRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OkrService {

    private final ObjectiveRepository objectiveRepository;
    private final KeyResultRepository keyResultRepository;

    @Transactional
    public Objective createObjective(UUID userId, String title, String description, LocalDate start, LocalDate end) {
        Objective objective = Objective.create(userId, title, description, start, end);
        return objectiveRepository.save(objective);
    }

    @Transactional
    public KeyResult addKeyResult(UUID objectiveId, String title, BigDecimal target, String unit, BigDecimal weight) {
        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new NotFoundException("Objective not found"));

        KeyResult keyResult = KeyResult.create(objectiveId, title, target, unit, weight);
        KeyResult saved = keyResultRepository.save(keyResult);

        recalculateProgress(objectiveId);

        return saved;
    }

    @Transactional
    public KeyResult updateKeyResultProgress(UUID keyResultId, BigDecimal newValue) {
        KeyResult keyResult = keyResultRepository.findById(keyResultId)
                .orElseThrow(() -> new NotFoundException("Key result not found"));

        KeyResult updated = keyResult.toBuilder()
                .currentValue(newValue)
                .build();

        KeyResult saved = keyResultRepository.save(updated);

        recalculateProgress(saved.getObjectiveId());

        return saved;
    }

    private void recalculateProgress(UUID objectiveId) {
        Objective objective = objectiveRepository.findById(objectiveId)
                .orElseThrow(() -> new NotFoundException("Objective not found"));

        List<KeyResult> keyResults = keyResultRepository.findByObjectiveId(objectiveId);

        if (keyResults.isEmpty()) {
            objectiveRepository.save(objective.toBuilder().progress(BigDecimal.ZERO).build());
            return;
        }

        BigDecimal totalWeight = keyResults.stream()
                .map(KeyResult::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal weightedProgressSum = keyResults.stream()
                .map(kr -> {
                    BigDecimal krProgress = kr.getCurrentValue()
                            .divide(kr.getTargetValue(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    return krProgress.multiply(kr.getWeight());
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProgress = weightedProgressSum.divide(totalWeight, 2, RoundingMode.HALF_UP);

        objectiveRepository.save(objective.toBuilder().progress(totalProgress).build());
    }

    public List<Objective> getUserObjectives(UUID userId) {
        return objectiveRepository.findByUserId(userId);
    }
}
