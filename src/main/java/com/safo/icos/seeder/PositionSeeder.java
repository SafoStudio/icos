package com.safo.icos.seeder;

import com.safo.icos.modules.org.infrastructure.repository.entity.PositionJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.jpa.PositionJpaRepository;
import com.safo.icos.seeder.dto.PositionSeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class PositionSeeder extends AbstractJsonSeeder<PositionSeedDto> {

  private final PositionJpaRepository positionRepository;

  public PositionSeeder(PositionJpaRepository positionRepository) {
    super("seed/positions.json", PositionSeedDto[].class);
    this.positionRepository = positionRepository;
  }

  @Override
  public void seed() {
    List<PositionSeedDto> dtos = loadDtos();
    if (dtos.isEmpty()) {
      log.warn("No positions found in seed data file.");
      return;
    }

    List<PositionJpaEntity> entities = dtos.stream()
            .map(dto -> PositionJpaEntity.builder()
                    .id(parseUuid(String.valueOf(dto.getId())))
                    .companyId(parseUuid(String.valueOf(dto.getCompanyId())))
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .createdAt(parseDateTime(dto.getCreatedAt()))
                    .updatedAt(parseDateTime(dto.getUpdatedAt()))
                    .build())
            .toList();

    positionRepository.saveAll(entities);
    log.info("Successfully seeded {} positions.", entities.size());
  }

  @Override
  public int getOrder() {
    return 3;
  }
}
