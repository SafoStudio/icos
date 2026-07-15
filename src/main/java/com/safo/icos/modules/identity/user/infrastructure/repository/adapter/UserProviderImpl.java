package com.safo.icos.modules.identity.user.infrastructure.repository.adapter;

import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserCreateDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.identity.user.api.dto.UserPrincipalDto;
import com.safo.icos.modules.identity.user.application.service.UserService;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.MembershipJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.MembershipJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.RoleJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserJpaRepository;
import java.util.List;
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
    private final MembershipJpaRepository membershipJpaRepository;
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
                .flatMap(role -> membershipJpaRepository.findFirstByDepartmentIdAndRoleIdAndActiveTrue(departmentId, role.getId()))
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
    public MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId, UUID departmentId, UUID managerId) {
        return userService.createMembership(userId, companyId, roleId, departmentId, managerId);
    }

    @Override
    public Optional<MembershipDto> findMembershipById(UUID membershipId) {
        return userService.findMembershipById(membershipId);
    }

  @Override
  public Optional<UserPrincipalDto> findPrincipal(UUID userId, UUID companyId) {
    return membershipJpaRepository.findActiveByUserIdAndCompanyId(userId, companyId)
            .map(membership -> {
              var user = userJpaRepository.findById(userId).orElseThrow();

              Set<String> permissions = roleJpaRepository.findById(membership.getRoleId())
                      .map(role -> role.getPermissions().stream()
                              .map(PermissionJpaEntity::getName)
                              .collect(Collectors.toSet()))
                      .orElse(Set.of());

              return UserPrincipalDto.builder()
                      .userId(user.getId())
                      .email(user.getEmail())
                      .membershipId(membership.getId())
                      .companyId(membership.getCompanyId())
                      .permissions(permissions)
                      .build();
            });
  }
}
