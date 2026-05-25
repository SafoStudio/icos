package com.safo.icos.modules.user.application;

import com.safo.icos.modules.user.api.dto.MembershipDto;
import com.safo.icos.modules.user.api.dto.UserDto;
import com.safo.icos.modules.user.api.dto.UserCreateDto;
import com.safo.icos.modules.user.application.dto.DocumentResponseDto;
import com.safo.icos.modules.user.application.dto.ProfileResponseDto;
import com.safo.icos.modules.user.application.dto.ProfileUpdateDto;
import com.safo.icos.modules.user.application.dto.UserResponseDto;
import com.safo.icos.modules.user.application.dto.UserUpdateDto;
import com.safo.icos.modules.user.domain.model.Email;
import com.safo.icos.modules.user.domain.model.Membership;
import com.safo.icos.modules.user.domain.model.User;
import com.safo.icos.modules.user.domain.repository.MembershipRepository;
import com.safo.icos.modules.user.domain.repository.UserRepository;
import com.safo.icos.modules.user.infrastructure.repository.*;
import com.safo.icos.security.permission.RequirePermission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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

  public UserDto createUserFromAuth(UserCreateDto data) {
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
      case "ACTIVE":
        user.activate();
        break;
      case "SUSPENDED":
        user.suspend();
        break;
      case "TERMINATED":
        user.terminate();
        break;
      default:
        throw new IllegalArgumentException("Invalid status");
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
            .collect(Collectors.toList());
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

  public void saveDocumentMetadata(UUID userId, String type, String filename, String path, String contentType, long size) {
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

  private UserResponseDto toResponseDto(User user) {
    ProfileResponseDto profileDto = profileRepository.findById(user.getId())
            .map(this::toProfileResponseDto)
            .orElse(null);

    List<DocumentResponseDto> documents = documentRepository.findAllByUserId(user.getId()).stream()
            .map(this::toDocumentResponseDto)
            .collect(Collectors.toList());

    return UserResponseDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail().value())
            .status(user.getStatus())
            .createdBy(user.getCreatedBy())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .profile(profileDto)
            .documents(documents)
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
