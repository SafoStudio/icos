package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
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
class DepartmentServiceIntegrationTest {

  @Autowired private DepartmentService departmentService;
  @Autowired private CompanyService companyService;
  @Autowired private DepartmentRepository departmentRepository;

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
  void shouldCreateDepartment() {
    DepartmentDto dept = departmentService.createDepartment("Engineering", "Dev team");

    assertThat(dept.getId()).isNotNull();
    assertThat(dept.getCompanyId()).isEqualTo(companyId);
    assertThat(dept.getName()).isEqualTo("Engineering");
    assertThat(dept.getDescription()).isEqualTo("Dev team");
  }

  @Test
  void shouldThrowWhenDuplicateDepartmentName() {
    departmentService.createDepartment("Engineering", "Dev team");

    assertThatThrownBy(() -> departmentService.createDepartment("Engineering", "Another"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already exists");
  }

  @Test
  void shouldUpdateDepartment() {
    DepartmentDto created = departmentService.createDepartment("Engineering", "Old description");

    DepartmentDto updated = departmentService.updateDepartment(created.getId(), "QA", "New description");

    assertThat(updated.getName()).isEqualTo("QA");
    assertThat(updated.getDescription()).isEqualTo("New description");
  }

  @Test
  void shouldThrowWhenUpdateNonExistentDepartment() {
    UUID nonExistentId = UUID.randomUUID();

    assertThatThrownBy(() -> departmentService.updateDepartment(nonExistentId, "QA", "Desc"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }

  @Test
  void shouldGetDepartmentsByCompany() {
    departmentService.createDepartment("Engineering", "Dev");
    departmentService.createDepartment("HR", "Human Resources");

    List<DepartmentDto> departments = departmentService.getDepartmentsByCompany(companyId);

    assertThat(departments).hasSize(2);
    assertThat(departments).extracting(DepartmentDto::getName)
            .containsExactlyInAnyOrder("Engineering", "HR");
  }
}
