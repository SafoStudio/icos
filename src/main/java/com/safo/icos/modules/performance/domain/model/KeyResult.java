package com.safo.icos.modules.performance.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class KeyResult {
    private final UUID id;
    private final UUID objectiveId;
    private final String title;
    private final BigDecimal targetValue;
    private final BigDecimal currentValue;
    private final String unit;
    private final BigDecimal weight;

    public static KeyResult create(UUID objectiveId, String title, BigDecimal target, String unit, BigDecimal weight) {
        return KeyResult.builder()
                .id(UUID.randomUUID())
                .objectiveId(objectiveId)
                .title(title)
                .targetValue(target)
                .currentValue(BigDecimal.ZERO)
                .unit(unit)
                .weight(weight != null ? weight : BigDecimal.ONE)
                .build();
    }
}
