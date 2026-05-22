package com.safo.icos.modules.recruitment.application.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVacancyRequest {
    private UUID companyId;
    private UUID departmentId;
    private UUID positionId;
    private String title;
    private String description;
    private String requirements;
}
