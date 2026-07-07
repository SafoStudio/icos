package com.safo.icos.seeder;

import com.safo.icos.modules.identity.user.infrastructure.repository.entity.MembershipJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.RoleJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.MembershipJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.RoleJpaRepository;
import com.safo.icos.seeder.dto.MembershipSeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class MembershipSeeder extends AbstractJsonSeeder<MembershipSeedDto> {

  private final MembershipJpaRepository membershipRepository;
  private final RoleJpaRepository roleRepository;

  public MembershipSeeder(
          MembershipJpaRepository membershipRepository,
          RoleJpaRepository roleRepository) {
    super("seed/memberships.json", MembershipSeedDto[].class);
    this.membershipRepository = membershipRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public void seed() {
    List<MembershipSeedDto> dtos = loadDtos();
    if (dtos.isEmpty()) {
      log.warn("No memberships found in seed data file.");
      return;
    }

    List<MembershipJpaEntity> entities = dtos.stream()
            .map(dto -> {
              UUID roleId = roleRepository.findByName(dto.getRoleName())
                      .map(RoleJpaEntity::getId)
                      .orElseThrow(() -> new IllegalArgumentException("Role not found: " + dto.getRoleName()));

              return MembershipJpaEntity.builder()
                      .id(parseUuid(String.valueOf(dto.getId())))
                      .userId(parseUuid(String.valueOf(dto.getUserId())))
                      .companyId(parseUuid(String.valueOf(dto.getCompanyId())))
                      .roleId(roleId)
                      .departmentId(dto.getDepartmentId() != null ? parseUuid(String.valueOf(dto.getDepartmentId())) : null)
                      .managerId(dto.getManagerId() != null ? parseUuid(String.valueOf(dto.getManagerId())) : null)
                      .active(dto.isActive())
                      .createdAt(parseDateTime(dto.getCreatedAt()))
                      .updatedAt(parseDateTime(dto.getUpdatedAt()))
                      .build();
            })
            .toList();

    membershipRepository.saveAll(entities);
    log.info("Successfully seeded {} memberships.", entities.size());
  }

  @Override
  public int getOrder() {
    return 5;
  }
}
