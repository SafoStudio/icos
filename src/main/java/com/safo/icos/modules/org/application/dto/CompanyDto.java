package com.safo.icos.modules.org.application.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class CompanyDto {
    private final UUID id;
    private final String name;
    private final String taxId;
}
