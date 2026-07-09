package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.PositionDto;
import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PositionServiceIntegrationTest {

  @Autowired private PositionService positionService;
  @Autowired private CompanyService companyService;

  private UUID companyId;

  @BeforeEach
  void setUp() {
    // Create company
    CompanyDto company = companyService.createCompanyInternal("TestCo", "123456789");
    companyId = company.getId();

    // Set tenant context
    TenantContext.setTenantId(companyId.toString());

    // Auth as ADMIN
    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(UUID.randomUUID())
            .email("admin@test.com")
            .membershipId(UUID.randomUUID())
            .companyId(companyId)
            .roleName("ADMIN")
            .authorities(List.of())
            .build();
    SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
    );
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldCreatePosition() {
    PositionDto position = positionService.createPosition("Developer", "Backend dev");

    assertThat(position.getId()).isNotNull();
    assertThat(position.getCompanyId()).isEqualTo(companyId);
    assertThat(position.getName()).isEqualTo("Developer");
    assertThat(position.getDescription()).isEqualTo("Backend dev");
  }

  @Test
  void shouldThrowWhenDuplicatePositionName() {
    positionService.createPosition("Developer", "Dev");

    assertThatThrownBy(() -> positionService.createPosition("Developer", "Another"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already exists");
  }

  @Test
  void shouldUpdatePosition() {
    PositionDto created = positionService.createPosition("Developer", "Old description");

    PositionDto updated = positionService.updatePosition(created.getId(), "Senior Developer", "Updated");

    assertThat(updated.getName()).isEqualTo("Senior Developer");
    assertThat(updated.getDescription()).isEqualTo("Updated");
  }

  @Test
  void shouldThrowWhenUpdateNonExistentPosition() {
    UUID nonExistentId = UUID.randomUUID();

    assertThatThrownBy(() -> positionService.updatePosition(nonExistentId, "Dev", "Desc"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldGetPositionsByCompany() {
    positionService.createPosition("Developer", "Dev");
    positionService.createPosition("QA", "Tester");

    List<PositionDto> positions = positionService.getPositionsByCompany(companyId);

    assertThat(positions).hasSize(2);
    assertThat(positions).extracting(PositionDto::getName)
            .containsExactlyInAnyOrder("Developer", "QA");
  }
}