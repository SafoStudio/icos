package com.safo.icos.modules.identity.user.infrastructure.repository.adapter;

import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserCreateDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.identity.user.api.dto.UserPrincipalDto;
import com.safo.icos.modules.identity.user.api.dto.UserProfileDto;
import com.safo.icos.modules.identity.user.application.UserService;
import com.safo.icos.modules.identity.user.application.dto.UserResponseDto;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.MembershipJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.RoleJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.MembershipJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.PermissionJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.RoleJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserJpaRepository;
import com.safo.icos.modules.org.infrastructure.repository.entity.DepartmentJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.jpa.DepartmentJpaRepository;
import com.safo.icos.modules.org.infrastructure.repository.jpa.DepartmentPermissionJpaRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserProviderAdapter implements UserProvider {
  private final UserService userService;
  private final MembershipJpaRepository membershipJpaRepository;
  private final PermissionJpaRepository permissionJpaRepository;
  private final DepartmentPermissionJpaRepository departmentPermissionJpaRepository;
  private final DepartmentJpaRepository departmentJpaRepository;
  private final RoleJpaRepository roleJpaRepository;
  private final UserJpaRepository userJpaRepository;

  @Override
  public Optional<UserDto> findByEmail(String email) {
    return userService.findByEmail(email);
  }

  @Override
  public Optional<UserDto> findById(UUID userId) {
    return userService.findById(userId);
  }

  @Override
  public Optional<UserResponseDto> findByIdWithProfile(UUID userId) {
    try {
      return Optional.of(userService.getUserById(userId));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<UserProfileDto> findProfileByUserId(UUID userId) {
    return userService.findProfileByUserId(userId);
  }

  @Override
  public UserDto createUser(UserCreateDto data) {
    return userService.createUserFromAuth(data);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userService.existsByEmail(email);
  }

  @Override
  public void updateUserStatus(UUID userId, String status) {
    userService.updateUserStatus(userId, status);
  }

  @Override
  public Optional<UserDto> findManagerByDepartmentId(UUID departmentId) {
    return roleJpaRepository.findByName("HEAD")
            .flatMap(role -> membershipJpaRepository
                    .findFirstByDepartmentIdAndRoleIdAndActiveTrue(departmentId, role.getId()))
            .map(MembershipJpaEntity::getUserId)
            .flatMap(userJpaRepository::findById)
            .map(entity -> UserDto.builder()
                    .id(entity.getId())
                    .email(entity.getEmail())
                    .firstName(entity.getFirstName())
                    .lastName(entity.getLastName())
                    .build());
  }

  @Override
  public Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId) {
    return userService.findActiveMembership(userId, companyId);
  }

  @Override
  public Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId) {
    return userService.findFirstActiveMembershipByUserId(userId);
  }

  @Override
  public MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId,
                                        UUID departmentId, UUID managerId) {
    return userService.createMembership(userId, companyId, roleId, departmentId, managerId);
  }

  @Override
  public Optional<MembershipDto> findMembershipById(UUID membershipId) {
    return userService.findMembershipById(membershipId);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<UserPrincipalDto> findPrincipal(UUID userId, UUID companyId) {
    return membershipJpaRepository.findActiveByUserIdAndCompanyId(userId, companyId)
            .map(membership -> {
              var user = userJpaRepository.findById(userId).orElseThrow();

              String roleName = roleJpaRepository.findById(membership.getRoleId())
                      .map(RoleJpaEntity::getName)
                      .orElse("");

              Set<String> permissions = new HashSet<>();

              // OWNER or ADMIN — all permissions
              if ("OWNER".equals(roleName) || "ADMIN".equals(roleName)) {
                permissions = permissionJpaRepository.findAll().stream()
                        .map(PermissionJpaEntity::getName)
                        .collect(Collectors.toSet());
              } else if (membership.getDepartmentId() != null) {
                // other role have local department_permissions
                permissions = departmentPermissionJpaRepository
                        .findByDepartmentIdAndRoleName(
                                membership.getDepartmentId(),
                                roleName
                        )
                        .stream()
                        .map(dp -> dp.getPermission().getName())
                        .collect(Collectors.toSet());
              }

              String departmentName = null;
              if (membership.getDepartmentId() != null) {
                departmentName = departmentJpaRepository.findById(membership.getDepartmentId())
                        .map(DepartmentJpaEntity::getName)
                        .orElse(null);
              }

              return UserPrincipalDto.builder()
                      .userId(user.getId())
                      .email(user.getEmail())
                      .membershipId(membership.getId())
                      .companyId(membership.getCompanyId())
                      .departmentId(membership.getDepartmentId())
                      .departmentName(departmentName)
                      .roleName(roleName)
                      .permissions(permissions)
                      .build();
            });
  }
}
