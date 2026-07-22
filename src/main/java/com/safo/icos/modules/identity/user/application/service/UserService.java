package com.safo.icos.modules.identity.user.application.service;

import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserCreateDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
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

/**
 * Application service for user and membership management.
 * Implements the {@link com.safo.icos.modules.identity.user.api.UserProvider}
 * contract (via a separate adapter) and provides additional user operations
 * such as profile updates and document management.
 */
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

  /**
   * Creates a new user during authentication (self-registration or invite acceptance).
   *
   * @param data user creation payload
   * @return the created user as a DTO
   */
  public UserDto createUserFromAuth(UserCreateDto data) {
    User user = userDomainService.createUser(
            UserName.of(data.getFirstName(), data.getLastName()),
            Email.of(data.getEmail()),
            data.getCreatedBy()
    );
    User saved = userRepository.save(user);
    return toUserDto(saved);
  }

  /**
   * Finds a user by email address.
   *
   * @param email the user's email
   * @return the user if found
   */
  public Optional<UserDto> findByEmail(String email) {
    return userRepository.findByEmail(Email.of(email)).map(this::toUserDto);
  }

  /**
   * Finds a user by their unique identifier.
   *
   * @param userId the user's UUID
   * @return the user if found
   */
  public Optional<UserDto> findById(UUID userId) {
    return userRepository.findById(userId).map(this::toUserDto);
  }

  /**
   * Checks if a user with the given email already exists.
   *
   * @param email the email to check
   * @return {@code true} if the email is in use
   */
  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(Email.of(email));
  }

  /**
   * Updates the status of a user.
   *
   * @param userId the user's UUID
   * @param status the new status value
   */
  public void updateUserStatus(UUID userId, String status) {
    User user = userDomainService.updateStatus(UserId.fromUUID(userId), status);
    userRepository.save(user);
  }

  /**
   * Finds an active membership for a user in a specific company.
   *
   * @param userId    the user's UUID
   * @param companyId the company UUID
   * @return the membership if found and active
   */
  public Optional<MembershipDto> findActiveMembership(UUID userId, UUID companyId) {
    return membershipRepository.findActiveByUserIdAndCompanyId(userId, companyId)
            .map(this::toMembershipDto);
  }

  /**
   * Finds the first active membership for a user across all companies.
   *
   * @param userId the user's UUID
   * @return the first active membership found
   */
  public Optional<MembershipDto> findFirstActiveMembershipByUserId(UUID userId) {
    return membershipRepository.findFirstByUserIdAndActiveTrue(userId)
            .map(this::toMembershipDto);
  }

  /**
   * Finds a membership by its unique identifier.
   *
   * @param membershipId the membership UUID
   * @return the membership if found
   */
  public Optional<MembershipDto> findMembershipById(UUID membershipId) {
    return membershipRepository.findById(membershipId)
            .map(this::toMembershipDto);
  }

  /**
   * Creates a new membership linking a user to a company.
   *
   * @param userId       the user's UUID
   * @param companyId    the company UUID
   * @param roleId       the role UUID
   * @param departmentId optional department assignment
   * @param managerId    optional manager assignment
   * @return the created membership
   */
  public MembershipDto createMembership(UUID userId, UUID companyId, UUID roleId,
                                        UUID departmentId, UUID managerId) {
    Membership membership = Membership.create(userId, companyId, roleId, departmentId, managerId);
    Membership saved = membershipRepository.save(membership);
    return toMembershipDto(saved);
  }

  /**
   * Finds a user by ID with full profile, documents, and membership data.
   *
   * @param userId the user's UUID
   * @return the user's full response DTO
   * @throws RuntimeException if the user is not found
   */
  public UserResponseDto getUserById(UUID userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    return toResponseDto(user);
  }

  /**
   * Returns all users belonging to a given company.
   *
   * @param companyId the company UUID
   * @return list of users with their membership details
   */
  public List<UserResponseDto> getUsersByCompanyId(UUID companyId) {
    List<Membership> memberships = membershipRepository.findAllByCompanyId(companyId);
    return memberships.stream()
            .map(m -> userRepository.findById(m.getUserId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(this::toResponseDto)
            .toList();
  }

  /**
   * Deletes a user by ID.
   *
   * @param userId the user's UUID
   */
  public void deleteUser(UUID userId) {
    userRepository.deleteById(userId);
  }

  /**
   * Updates a user's name fields. Requires {@code user:edit} permission.
   *
   * @param userId the user's UUID
   * @param input  the fields to update
   * @return the updated user
   */
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

  /**
   * Updates a user's profile fields. Requires {@code profile:edit} permission.
   *
   * @param userId the user's UUID
   * @param input  the profile fields to update
   * @return the updated profile
   */
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

  /**
   * Updates the avatar file path for a user.
   *
   * @param userId the user's UUID
   * @param path   the file path to the avatar image
   */
  public void updateAvatarPath(UUID userId, String path) {
    UserProfileJpaEntity profile = profileRepository.findById(userId)
            .orElse(UserProfileJpaEntity.builder().userId(userId).build());
    profile.setAvatarPath(path);
    profileRepository.save(profile);
  }

  /**
   * Saves or updates document metadata for a user.
   *
   * @param userId      the user's UUID
   * @param type        document type (e.g., "passport", "diploma")
   * @param filename    original filename
   * @param path        storage file path
   * @param contentType MIME type
   * @param size        file size in bytes
   */
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

  /**
   * Deletes document metadata for a user.
   *
   * @param userId   the user's UUID
   * @param type     document type
   * @param filename document filename
   */
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
