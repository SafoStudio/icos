package com.safo.icos.modules.timeoff.domain.model;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaveType {
    private final UUID id;
    private final String name;
    private final String code;
    private final boolean requiresBalance;
    private final String color;
    private final boolean isActive;
}
