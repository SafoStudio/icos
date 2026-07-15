package com.safo.icos.modules.org.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.domain.model.Department;
import com.safo.icos.modules.org.domain.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

  @Mock private DepartmentRepository departmentRepository;

  @InjectMocks
  private DepartmentService departmentService;

  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID DEPT_ID = UUID.randomUUID();

  @Test
  void shouldReturnDepartmentsByCompany() {
    Department dept = Department.create(COMPANY_ID, "Engineering", "Dev");
    when(departmentRepository.findByCompanyId(COMPANY_ID)).thenReturn(List.of(dept));

    List<DepartmentDto> result = departmentService.getDepartmentsByCompany(COMPANY_ID);

    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("Engineering");
  }

  @Test
  void shouldReturnEmptyListWhenNoDepartments() {
    when(departmentRepository.findByCompanyId(COMPANY_ID)).thenReturn(null);

    List<DepartmentDto> result = departmentService.getDepartmentsByCompany(COMPANY_ID);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldUpdateDepartment() {
    Department dept = Department.create(COMPANY_ID, "Engineering", "Old");
    when(departmentRepository.findById(DEPT_ID)).thenReturn(Optional.of(dept));
    when(departmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    DepartmentDto result = departmentService.updateDepartment(DEPT_ID, "QA", "New");

    assertThat(result.getName()).isEqualTo("QA");
    assertThat(result.getDescription()).isEqualTo("New");
    verify(departmentRepository).save(any());
  }

  @Test
  void shouldThrowWhenUpdateDepartmentNotFound() {
    when(departmentRepository.findById(DEPT_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> departmentService.updateDepartment(DEPT_ID, "QA", "New"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }
}
