package com.safo.icos.modules.user.infrastructure.adapter;

import com.safo.icos.modules.user.api.dto.MembershipDto;
import com.safo.icos.modules.user.api.dto.UserDto;
import com.safo.icos.modules.user.api.dto.UserCreateDto;
import com.safo.icos.modules.user.api.UserProvider;
import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.modules.user.infrastructure.repository.DepartmentJpaEntity;
import com.safo.icos.modules.user.infrastructure.repository.DepartmentJpaRepository;
import com.safo.icos.modules.user.infrastructure.repository.MembershipJpaRepository;
import com.safo.icos.modules.user.infrastructure.repository.PermissionJpaEntity;
import com.safo.icos.modules.user.infrastructure.repository.RoleJpaRepository;
import com.safo.icos.modules.user.infrastructure.repository.UserJpaRepository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {

  private final UserService userService;
  private final UserJpaRepository userJpaRepository;
  private final MembershipJpaRepository membershipJpaRepository;
  private final RoleJpaRepository roleJpaRepository;
  private final DepartmentJpaRepository departmentJpaRepository;

  @Override
  public Optional<UserDto> findByEmail(String email) {
    return userService.findByEmail(email);
  }

  @Override
  public Optional<UserDto> findById(UUID userId) {
    return userService.findById(userId);
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
  public Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId) {
    return userService.findActiveMembership(userId, companyId);
  }

  @Override
  public Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId) {
    return userService.findFirstActiveMembershipByUserId(userId);
  }

  @Override
  public MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId, UUID departmentId, UUID managerId) {
    return userService.createMembership(userId, companyId, roleId, departmentId, managerId);
  }

  @Override
  public Optional<MembershipDto> findMembershipById(UUID membershipId) {
    return userService.findMembershipById(membershipId);
  }

  @Override
  public Optional<com.safo.icos.modules.user.api.dto.UserPrincipalDto> findPrincipal(UUID userId, UUID companyId) {
    return userJpaRepository.findById(userId).flatMap(user ->
            membershipJpaRepository.findActiveByUserIdAndCompanyId(userId, companyId).flatMap(membership ->
                    roleJpaRepository.findById(membership.getRoleId()).map(role -> {
                      Set<String> permissions = role.getPermissions().stream()
                              .map(PermissionJpaEntity::getName)
                              .collect(Collectors.toSet());

                      String departmentName = null;
                      if (membership.getDepartmentId() != null) {
                        departmentName = departmentJpaRepository.findById(membership.getDepartmentId())
                                .map(DepartmentJpaEntity::getName)
                                .orElse(null);
                      }

                      return com.safo.icos.modules.user.api.dto.UserPrincipalDto.builder()
                              .userId(user.getId())
                              .email(user.getEmail())
                              .membershipId(membership.getId())
                              .companyId(membership.getCompanyId())
                              .roleName(role.getName())
                              .departmentId(membership.getDepartmentId())
                              .departmentName(departmentName)
                              .permissions(permissions)
                              .build();
                    })
            )
    );
  }
}
