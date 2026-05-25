package com.safo.icos.modules.talent.onboarding.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class OnboardingTemplateDto {
    private final UUID id;
    private final String name;
    private final String description;
}
