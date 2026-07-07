package com.safo.icos.seeder;

import com.safo.icos.modules.org.infrastructure.repository.DepartmentJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.DepartmentJpaRepository;
import com.safo.icos.seeder.dto.DepartmentSeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class DepartmentSeeder extends AbstractJsonSeeder<DepartmentSeedDto> {

  private final DepartmentJpaRepository departmentRepository;

  public DepartmentSeeder(DepartmentJpaRepository departmentRepository) {
    super("seed/departments.json", DepartmentSeedDto[].class);
    this.departmentRepository = departmentRepository;
  }

  @Override
  public void seed() {
    List<DepartmentSeedDto> dtos = loadDtos();
    if (dtos.isEmpty()) {
      log.warn("No departments found in seed data file.");
      return;
    }

    List<DepartmentJpaEntity> entities = dtos.stream()
            .map(dto -> DepartmentJpaEntity.builder()
                    .id(parseUuid(String.valueOf(dto.getId())))
                    .companyId(parseUuid(String.valueOf(dto.getCompanyId())))
                    .parentId(dto.getParentId() != null ? parseUuid(String.valueOf(dto.getParentId())) : null)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .createdAt(parseDateTime(dto.getCreatedAt()))
                    .updatedAt(parseDateTime(dto.getUpdatedAt()))
                    .build())
            .toList();

    departmentRepository.saveAll(entities);
    log.info("Successfully seeded {} departments.", entities.size());
  }

  @Override
  public int getOrder() {
    return 2;
  }
}
