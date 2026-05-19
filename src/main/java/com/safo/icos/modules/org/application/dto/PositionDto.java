package com.safo.icos.modules.org.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class PositionDto {
    private final UUID id;
    private final UUID companyId;
    private final String name;
    private final String description;
}
