package com.safo.icos.seeder;

import com.safo.icos.modules.org.infrastructure.repository.CompanyJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.CompanyJpaRepository;
import com.safo.icos.seeder.dto.CompanySeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class CompanySeeder extends AbstractJsonSeeder<CompanySeedDto> {

  private final CompanyJpaRepository companyRepository;

  public CompanySeeder(CompanyJpaRepository companyRepository) {
    super("seed/companies.json", CompanySeedDto[].class);
    this.companyRepository = companyRepository;
  }

  @Override
  public void seed() {
    List<CompanySeedDto> dtos = loadDtos();
    if (dtos.isEmpty()) {
      log.warn("No companies found in seed data file.");
      return;
    }

    List<CompanyJpaEntity> entities = dtos.stream()
            .map(dto -> CompanyJpaEntity.builder()
                    .id(parseUuid(String.valueOf(dto.getId())))
                    .name(dto.getName())
                    .taxId(dto.getTaxId())
                    .policyVersion(dto.getPolicyVersion() != null ? dto.getPolicyVersion() : 1)
                    .createdAt(parseDateTime(dto.getCreatedAt()))
                    .updatedAt(parseDateTime(dto.getUpdatedAt()))
                    .build())
            .toList();

    companyRepository.saveAll(entities);
    log.info("Successfully seeded {} companies.", entities.size());
  }

  @Override
  public int getOrder() {
    return 1;
  }
}
