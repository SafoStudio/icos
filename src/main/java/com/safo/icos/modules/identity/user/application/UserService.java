package com.safo.icos.modules.identity.user.application;

import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserCreateDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.identity.user.api.dto.UserProfileDto;
import com.safo.icos.modules.identity.user.application.dto.DocumentResponseDto;
import com.safo.icos.modules.identity.user.application.dto.ProfileResponseDto;
import com.safo.icos.modules.identity.user.application.dto.ProfileUpdateDto;
import com.safo.icos.modules.identity.user.application.dto.UserResponseDto;
import com.safo.icos.modules.identity.user.application.dto.UserUpdateDto;
import com.safo.icos.modules.identity.user.domain.model.Email;
import com.safo.icos.modules.identity.user.domain.model.Membership;
import com.safo.icos.modules.identity.user.domain.model.User;
import com.safo.icos.modules.identity.user.domain.model.UserId;
import com.safo.icos.modules.identity.user.domain.model.UserName;
import com.safo.icos.modules.identity.user.domain.repository.MembershipRepository;
import com.safo.icos.modules.identity.user.domain.repository.UserRepository;
import com.safo.icos.modules.identity.user.domain.service.UserDomainService;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.RoleJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserDocumentJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.UserProfileJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.RoleJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserDocumentJpaRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.UserProfileJpaRepository;
import com.safo.icos.security.permission.RequirePermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final MembershipRepository membershipRepository;
  private final RoleJpaRepository roleJpaRepository;
  private final UserProfileJpaRepository profileRepository;
  private final UserDocumentJpaRepository documentRepository;
  private final UserDomainService userDomainService;

  public UserDto createUserFromAuth(UserCreateDto data) {
    User user = userDomainService.createUser(
            UserName.of(data.getFirstName(), data.getLastName()),
            Email.of(data.getEmail()),
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

  public Optional<UserProfileDto> findProfileByUserId(UUID userId) {
    return profileRepository.findById(userId)
            .map(entity -> UserProfileDto.builder()
                    .avatarPath(entity.getAvatarPath())
                    .phoneNumber(entity.getPhoneNumber())
                    .address(entity.getAddress())
                    .birthDate(entity.getBirthDate())
                    .hireDate(entity.getHireDate())
                    .build());
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(Email.of(email));
  }

  public void updateUserStatus(UUID userId, String status) {
    User user = userDomainService.updateStatus(UserId.fromUUID(userId), status);
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

  public MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId,
                                        UUID departmentId, UUID managerId) {
    Membership membership = Membership.create(userId, companyId, roleId, departmentId, managerId);
    Membership saved = membershipRepository.save(membership);
    return toMembershipDto(saved);
  }

  public UserResponseDto getUserById(UUID userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    return toResponseDto(user);
  }

  public List<UserResponseDto> getUsersByCompanyId(UUID companyId) {
    List<Membership> memberships = membershipRepository.findAllByCompanyId(companyId);
    return memberships.stream()
            .map(m -> userRepository.findById(m.getUserId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::toResponseDto)
            .toList();
  }

  public void deleteUser(UUID userId) {
    userRepository.deleteById(userId);
  }

  @RequirePermission("user:edit")
  public UserResponseDto updateUser(UUID userId, UserUpdateDto input) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (input.getFirstName() != null) {
      user.updateName(input.getFirstName(), user.getLastName());
    }
    if (input.getLastName() != null) {
      user.updateName(user.getFirstName(), input.getLastName());
    }

    if (input.getRole() != null || input.getDepartmentId() != null) {
      membershipRepository.findFirstByUserIdAndActiveTrue(userId).ifPresent(membership -> {
        boolean updated = false;
        if (input.getRole() != null && !input.getRole().isBlank()) {
          roleJpaRepository.findByName(input.getRole()).ifPresent(roleEntity -> {
            membership.changeRole(roleEntity.getId());
          });
          updated = true;
        }
        if (input.getDepartmentId() != null) {
          membership.changeDepartment(input.getDepartmentId());
          updated = true;
        }
        if (updated) {
          membershipRepository.save(membership);
        }
      });
    }

    User saved = userRepository.save(user);
    return toResponseDto(saved);
  }

  @RequirePermission("profile:edit")
  public ProfileResponseDto updateProfile(UUID userId, ProfileUpdateDto input) {
    UserProfileJpaEntity profile = profileRepository.findById(userId)
            .orElse(UserProfileJpaEntity.builder().userId(userId).build());

    if (input.getPhoneNumber() != null) profile.setPhoneNumber(input.getPhoneNumber());
    if (input.getAddress() != null) profile.setAddress(input.getAddress());
    if (input.getHireDate() != null) profile.setHireDate(input.getHireDate());
    if (input.getBirthDate() != null) profile.setBirthDate(input.getBirthDate());

    UserProfileJpaEntity saved = profileRepository.save(profile);
    return toProfileResponseDto(saved);
  }

  public void updateAvatarPath(UUID userId, String path) {
    UserProfileJpaEntity profile = profileRepository.findById(userId)
            .orElse(UserProfileJpaEntity.builder().userId(userId).build());
    profile.setAvatarPath(path);
    profileRepository.save(profile);
  }

  public void saveDocumentMetadata(UUID userId, String type, String filename,
                                   String path, String contentType, long size) {
    documentRepository.findByUserIdAndTypeAndFilename(userId, type, filename)
            .ifPresentOrElse(
                    doc -> {
                      doc.setFilePath(path);
                      doc.setContentType(contentType);
                      doc.setFileSize(size);
                      documentRepository.save(doc);
                    },
                    () -> {
                      UserDocumentJpaEntity doc = UserDocumentJpaEntity.builder()
                              .userId(userId)
                              .type(type)
                              .filename(filename)
                              .filePath(path)
                              .contentType(contentType)
                              .fileSize(size)
                              .build();
                      documentRepository.save(doc);
                    }
            );
  }

  public void deleteDocumentMetadata(UUID userId, String type, String filename) {
    documentRepository.deleteByUserIdAndTypeAndFilename(userId, type, filename);
  }

  // ---------- private mapping methods ----------

  private UserDto toUserDto(User user) {
    return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail().value())
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

  private UserResponseDto toResponseDto(User user) {
    ProfileResponseDto profileDto = profileRepository.findById(user.getId())
            .map(this::toProfileResponseDto)
            .orElse(null);

    List<DocumentResponseDto> documents = documentRepository.findAllByUserId(user.getId()).stream()
            .map(this::toDocumentResponseDto)
            .toList();

    var membershipOpt = membershipRepository.findFirstByUserIdAndActiveTrue(user.getId());
    String roleName = membershipOpt.map(m -> roleJpaRepository.findById(m.getRoleId())
                    .map(RoleJpaEntity::getName)
                    .orElse("SPECIALIST"))
            .orElse("SPECIALIST");

    UUID departmentId = membershipOpt.map(Membership::getDepartmentId).orElse(null);

    List<String> permissions = membershipOpt.map(m -> roleJpaRepository.findById(m.getRoleId())
                    .map(role -> role.getPermissions().stream()
                            .map(PermissionJpaEntity::getName)
                            .toList())
                    .orElse(List.of()))
            .orElse(List.of());

    UUID companyId = membershipOpt.map(Membership::getCompanyId).orElse(null);

    return UserResponseDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail().value())
            .status(user.getStatus())
            .companyId(companyId)
            .createdBy(user.getCreatedBy())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .profile(profileDto)
            .documents(documents)
            .role(roleName)
            .departmentId(departmentId)
            .permissions(permissions)
            .build();
  }

  private ProfileResponseDto toProfileResponseDto(UserProfileJpaEntity entity) {
    return ProfileResponseDto.builder()
            .userId(entity.getUserId())
            .phoneNumber(entity.getPhoneNumber())
            .address(entity.getAddress())
            .hireDate(entity.getHireDate())
            .birthDate(entity.getBirthDate())
            .avatarPath(entity.getAvatarPath())
            .build();
  }

  private DocumentResponseDto toDocumentResponseDto(UserDocumentJpaEntity entity) {
    return DocumentResponseDto.builder()
            .id(entity.getId())
            .type(entity.getType())
            .filename(entity.getFilename())
            .filePath(entity.getFilePath())
            .contentType(entity.getContentType())
            .fileSize(entity.getFileSize())
            .createdAt(entity.getCreatedAt())
            .build();
  }
}