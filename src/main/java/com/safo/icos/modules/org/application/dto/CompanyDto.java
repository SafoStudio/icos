package com.safo.icos.modules.org.application.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyDto {
  private final UUID id;
  private final String name;
  private final String taxId;
}
