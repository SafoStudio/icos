package com.safo.icos.modules.user.infrastructure.adapter;

import com.safo.icos.common.dto.MembershipDto;
import com.safo.icos.common.dto.UserDto;
import com.safo.icos.common.port.user.UserCreateData;
import com.safo.icos.common.port.user.UserProvider;
import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.modules.user.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public UserDto createUser(UserCreateData data) {
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
    public MembershipDto createMembership(UUID userId, UUID companyId, String role, UUID departmentId) {
        return userService.createMembership(userId, companyId, role, departmentId);
    }

    @Override
    public Optional<MembershipDto> findMembershipById(UUID membershipId) {
        return userService.findMembershipById(membershipId);
    }

    @Override
    public Optional<com.safo.icos.common.port.user.UserPrincipalData> findPrincipal(UUID userId, UUID companyId) {
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

                    return com.safo.icos.common.port.user.UserPrincipalData.builder()
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
