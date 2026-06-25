package com.safo.icos.modules.talent.onboarding.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OnboardingTemplateDto {
    private final UUID id;
    private final String name;
    private final String description;
}
