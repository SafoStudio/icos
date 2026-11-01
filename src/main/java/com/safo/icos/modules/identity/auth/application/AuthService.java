package com.safo.icos.modules.identity.auth.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.auth.api.AuthTokenProvider;
import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;
import com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto;
import com.safo.icos.modules.identity.auth.application.dto.*;
import com.safo.icos.modules.identity.auth.domain.exception.InvalidCredentialsException;
import com.safo.icos.modules.identity.auth.domain.exception.UserAlreadyExistsException;
import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;
import com.safo.icos.modules.identity.auth.domain.model.TokenValue;
import com.safo.icos.modules.identity.auth.domain.repository.RefreshTokenRepository;
import com.safo.icos.modules.identity.auth.domain.service.AuthDomainService;
import com.safo.icos.modules.identity.auth.domain.service.RefreshTokenCreationResult;
import com.safo.icos.modules.identity.auth.infrastructure.repository.entity.AuthCredentialsJpaEntity;
import com.safo.icos.modules.identity.auth.infrastructure.repository.jpa.AuthCredentialsJpaRepository;
import com.safo.icos.modules.identity.invite.application.dto.InviteAcceptDto;
import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.PermissionJpaRepository;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.constant.UserErrorCode;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserCreateDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.identity.user.api.dto.UserProfileDto;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.RoleJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.RoleJpaRepository;
import com.safo.icos.modules.org.infrastructure.repository.jpa.DepartmentPermissionJpaRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final UserProvider userProvider;
  private final AuthTokenProvider authTokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final AuthDomainService authDomainService;
  private final PasswordEncoder passwordEncoder;
  private final AuthCredentialsJpaRepository credentialsRepository;
  private final CompanyService companyService;
  private final RoleJpaRepository roleJpaRepository;
  private final InviteRepository inviteRepository;
  private final DepartmentPermissionJpaRepository departmentPermissionJpaRepository;
  private final PermissionJpaRepository permissionJpaRepository;

  @Value("${app.auth.refresh-token-ttl:30d}")
  private Duration refreshTokenTtl;

  public AuthResponseDto register(RegisterRequestDto request, String deviceInfo) {
    if (userProvider.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
    }

    UserDto user = createUserFromRequest(request);
    savePassword(user.getId(), request.getPassword());
    CompanyDto company = companyService.createCompanyInternal(request.getCompanyName(), null);
    MembershipDto membership = createOwnerMembership(user.getId(), company.getId());
    UserDto userWithCompany = enrichUserWithCompanyAndProfile(user, membership);

    RefreshTokenCreationResult rtResult = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(rtResult.entity());

    AuthTokenDto tokens = authTokenProvider.generateAccessToken(user.getId(), membership.getCompanyId());

    return AuthResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(rtResult.rawToken())
            .tokenType(tokens.getTokenType())
            .expiresIn(tokens.getExpiresIn())
            .user(userWithCompany)
            .build();
  }

  public AuthResponseDto login(LoginRequestDto request, String deviceInfo) {
    UserDto user = userProvider.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
    validatePassword(user.getId(), request.getPassword());

    MembershipDto membership = userProvider.findFirstActiveMembershipByUserId(user.getId())
            .orElseThrow(() -> new BusinessException(
                    "User does not have an active membership",
                    UserErrorCode.USER_NOT_ACTIVE));

    RefreshTokenCreationResult rtResult = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(rtResult.entity());

    AuthTokenDto tokens = authTokenProvider.generateAccessToken(user.getId(), membership.getCompanyId());
    UserDto userWithCompany = enrichUserWithCompanyAndProfile(user, membership);

    return AuthResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(rtResult.rawToken())
            .tokenType(tokens.getTokenType())
            .expiresIn(tokens.getExpiresIn())
            .user(userWithCompany)
            .build();
  }

  public RefreshResponseDto refresh(String refreshTokenValue, String deviceInfo) {
    TokenValue tokenHash = TokenValue.fromRaw(refreshTokenValue);
    RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new BusinessException("Invalid refresh token", AuthErrorCode.TOKEN_NOT_FOUND));

    authDomainService.validateRefreshToken(refreshToken);

    refreshTokenRepository.save(refreshToken.revoke());

    MembershipDto membership = userProvider.findMembershipById(refreshToken.getMembershipId())
            .orElseThrow(() -> new BusinessException("Membership not found", UserErrorCode.USER_NOT_FOUND));
    UserDto user = userProvider.findById(membership.getUserId())
            .orElseThrow(() -> new BusinessException("User not found", UserErrorCode.USER_NOT_FOUND));

    RefreshTokenCreationResult newRtResult = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(newRtResult.entity());

    AuthTokenDto tokens = authTokenProvider.generateAccessToken(user.getId(), membership.getCompanyId());

    return RefreshResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(newRtResult.rawToken())
            .expiresIn(tokens.getExpiresIn())
            .build();
  }

  public void logout(String refreshTokenValue) {
    TokenValue tokenHash = TokenValue.fromRaw(refreshTokenValue);
    refreshTokenRepository.findByTokenHash(tokenHash)
            .ifPresent(token -> refreshTokenRepository.save(token.revoke()));
  }

  public AuthResponseDto registerFromInvite(Invite invite, InviteAcceptDto request, String deviceInfo) {
    if (userProvider.existsByEmail(invite.getEmail())) {
      throw new UserAlreadyExistsException("User with email " + invite.getEmail() + " already exists");
    }

    UserDto user = createUserFromInvite(invite, request);
    savePassword(user.getId(), request.password());

    UUID roleId = roleJpaRepository.findByName(invite.getRole().name())
            .map(RoleJpaEntity::getId)
            .orElseThrow(() -> new RuntimeException("Role " + invite.getRole().name() + " not found"));

    MembershipDto membership = userProvider.createMembership(
            user.getId(), invite.getCompanyId(), roleId, invite.getDepartmentId(), invite.getManagerId());
    UserDto userWithCompany = enrichUserWithCompanyAndProfile(user, membership);

    invite.accept();
    inviteRepository.save(invite);

    RefreshTokenCreationResult rtResult = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(rtResult.entity());

    AuthTokenDto tokens = authTokenProvider.generateAccessToken(user.getId(), membership.getCompanyId());

    return AuthResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(rtResult.rawToken())
            .tokenType(tokens.getTokenType())
            .expiresIn(tokens.getExpiresIn())
            .user(userWithCompany)
            .build();
  }

  private UserDto createUserFromRequest(RegisterRequestDto request) {
    UserCreateDto createData = UserCreateDto.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .createdBy(null)
            .build();
    return userProvider.createUser(createData);
  }

  private UserDto createUserFromInvite(Invite invite, InviteAcceptDto request) {
    UserCreateDto createData = UserCreateDto.builder()
            .email(invite.getEmail())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .createdBy(invite.getInvitedById())
            .build();
    return userProvider.createUser(createData);
  }

  private void savePassword(UUID userId, String rawPassword) {
    AuthCredentialsJpaEntity credentials = AuthCredentialsJpaEntity.builder()
            .userId(userId)
            .passwordHash(passwordEncoder.encode(rawPassword))
            .build();
    credentialsRepository.save(credentials);
  }

  private void validatePassword(UUID userId, String rawPassword) {
    AuthCredentialsJpaEntity credentials = credentialsRepository.findById(userId)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
    if (!passwordEncoder.matches(rawPassword, credentials.getPasswordHash())) {
      throw new InvalidCredentialsException("Invalid email or password");
    }
  }

  private MembershipDto createOwnerMembership(UUID userId, UUID companyId) {
    UUID ownerRoleId = roleJpaRepository.findByName("OWNER")
            .map(RoleJpaEntity::getId)
            .orElseThrow(() -> new RuntimeException("Role OWNER not found"));
    return userProvider.createMembership(userId, companyId, ownerRoleId, null, null);
  }

  private UserDto enrichUserWithCompanyAndProfile(UserDto user, MembershipDto membership) {
    String roleName = roleJpaRepository.findById(membership.getRoleId())
            .map(RoleJpaEntity::getName)
            .orElse("");

    Set<String> permissions = new HashSet<>();

    if ("OWNER".equals(roleName) || "ADMIN".equals(roleName)) {
      permissions = permissionJpaRepository.findAll().stream()
              .map(PermissionJpaEntity::getName)
              .collect(Collectors.toSet());
    }
    else if (membership.getDepartmentId() != null) {
      permissions = departmentPermissionJpaRepository
              .findByDepartmentIdAndRoleName(membership.getDepartmentId(), roleName)
              .stream()
              .map(dp -> dp.getPermission().getName())
              .collect(Collectors.toSet());
    }

    UserProfileDto profile = userProvider.findProfileByUserId(user.getId()).orElse(null);

    return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .companyId(membership.getCompanyId().toString())
            .role(roleName)
            .departmentId(membership.getDepartmentId())
            .permissions(new ArrayList<>(permissions))
            .status(user.getStatus())
            .profile(profile)
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
  }
}