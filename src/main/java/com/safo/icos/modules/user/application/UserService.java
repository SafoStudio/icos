package com.safo.icos.modules.user.application;

import com.safo.icos.common.dto.MembershipDto;
import com.safo.icos.common.dto.UserDto;
import com.safo.icos.common.port.user.UserCreateData;
import com.safo.icos.modules.user.application.dto.UserResponseDto;
import com.safo.icos.modules.user.domain.model.Email;
import com.safo.icos.modules.user.domain.model.Membership;
import com.safo.icos.modules.user.domain.model.User;
import com.safo.icos.modules.user.domain.model.UserStatus;
import com.safo.icos.modules.user.domain.repository.MembershipRepository;
import com.safo.icos.modules.user.domain.repository.UserRepository;
import com.safo.icos.modules.user.infrastructure.repository.*;
import com.safo.icos.security.permission.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserProfileJpaRepository profileRepository;

    public UserDto createUserFromAuth(UserCreateData data) {
        Email email = Email.of(data.getEmail());

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.create(
                data.getFirstName(),
                data.getLastName(),
                email,
                data.getCreatedBy()
        );

        User saved = userRepository.save(user);
        return toUserDto(saved);
    }

    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(Email.of(email)).map(this::toUserDto);
    }

    public Optional<UserDto> findById(UUID userId) {
        return userRepository.findById(userId).map(this::toUserDto);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(Email.of(email));
    }

    public void updateUserStatus(UUID userId, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        switch (status.toUpperCase()) {
            case "ACTIVE": user.activate(); break;
            case "SUSPENDED": user.suspend(); break;
            case "TERMINATED": user.terminate(); break;
            default: throw new IllegalArgumentException("Invalid status");
        }

        userRepository.save(user);
    }

    public Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId) {
        return membershipRepository.findActiveByUserIdAndCompanyId(userId, companyId)
                .map(this::toMembershipDto);
    }

    public Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId) {
        return membershipRepository.findFirstByUserIdAndActiveTrue(userId)
                .map(this::toMembershipDto);
    }

    public Optional<MembershipDto> findMembershipById(UUID membershipId) {
        return membershipRepository.findById(membershipId)
                .map(this::toMembershipDto);
    }

    public MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId, UUID departmentId, UUID managerId) {
        Membership membership = Membership.create(userId, companyId, roleId, departmentId, managerId);
        Membership saved = membershipRepository.save(membership);
        return toMembershipDto(saved);
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail().getValue())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private MembershipDto toMembershipDto(Membership membership) {
        String roleName = roleJpaRepository.findById(membership.getRoleId())
                .map(RoleJpaEntity::getName)
                .orElse("UNKNOWN");

        return MembershipDto.builder()
                .id(membership.getId())
                .userId(membership.getUserId())
                .companyId(membership.getCompanyId())
                .role(roleName)
                .roleId(membership.getRoleId())
                .departmentId(membership.getDepartmentId())
                .active(membership.isActive())
                .build();
    }

    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toResponseDto(user);
    }

    public List<UserResponseDto> getUsersByCompanyId(UUID companyId) {
        return List.of();
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail().getValue())
                .status(user.getStatus())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @RequirePermission("profile:edit")
    public void updateProfile(UUID userId, String phoneNumber, String address, String bio) {
        UserProfileJpaEntity profile = profileRepository.findById(userId)
                .orElse(UserProfileJpaEntity.builder().userId(userId).build());

        profile.setPhoneNumber(phoneNumber);
        profile.setAddress(address);
        profile.setBio(bio);

        profileRepository.save(profile);
    }
}
