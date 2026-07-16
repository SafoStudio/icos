package com.safo.icos.modules.org.application;

import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.PermissionJpaRepository;
import com.safo.icos.modules.org.application.dto.PermissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PermissionService provides access to the global list of permissions available in the system.
 *
 * <p><b>Why is this service located in the Org module?</b></p>
 * <ul>
 *   <li>Permissions are primarily used for <b>department-level access control</b> via
 *       {@code department_permissions} table, which is part of the Org module.</li>
 *   <li>Although {@code PermissionJpaEntity} resides in the Identity module,
 *       its usage in the Org context is focused on <b>assigning permissions to departments</b>.</li>
 *   <li>Moving this service to a separate module would create an unnecessary circular dependency
 *       between Identity and Org. Currently, Org depends on Identity (for Permission entity),
 *       which is a one-way dependency and is acceptable.</li>
 * </ul>
 *
 * <p><b>Alternative approach considered:</b></p>
 * <ul>
 *   <li>Exposing permissions via Identity module's own service — rejected because:
 *       <ol>
 *         <li>Identity module does not need to know about department permissions.</li>
 *         <li>It would create unnecessary coupling for a purely Org-specific use case.</li>
 *       </ol>
 *   </li>
 *   <li>Creating a separate {@code PermissionModule} — rejected as over-engineering
 *       for the current scope. Permissions are not a standalone business domain;
 *       they are metadata used by multiple modules.</li>
 * </ul>
 *
 * <p><b>Current design decision:</b></p>
 * <ul>
 *   <li>Keep {@code PermissionService} in the Org module as a lightweight facade
 *       over {@code PermissionJpaRepository}.</li>
 *   <li>This aligns with the principle that Org is the primary consumer of permissions
 *       for department-level RBAC.</li>
 *   <li>If other modules (e.g., Recruitment, Onboarding) need permissions in the future,
 *       they can either depend on Org module or directly use {@code PermissionJpaRepository}
 *       via Identity module.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity
 * @see com.safo.icos.modules.org.application.DepartmentService
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

  private final PermissionJpaRepository permissionJpaRepository;

  /**
   * Returns all permissions available in the system.
   * Used primarily for populating the department permissions UI.
   *
   * @return list of all permissions with id, name, and description
   */
  public List<PermissionDto> getAllPermissions() {
    return permissionJpaRepository.findAll().stream()
            .map(entity -> PermissionDto.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .build())
            .toList();
  }
}