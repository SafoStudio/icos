package com.safo.icos.modules.auth.application;

import com.safo.icos.modules.auth.api.constant.AuthErrorCode;
import com.safo.icos.modules.user.api.constant.UserErrorCode;
import com.safo.icos.modules.auth.api.dto.AuthTokenDto;
import com.safo.icos.modules.user.api.dto.MembershipDto;
import com.safo.icos.modules.user.api.dto.UserDto;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.auth.api.AuthTokenProvider;
import com.safo.icos.modules.user.api.dto.UserCreateDto;
import com.safo.icos.modules.user.api.UserProvider;
import com.safo.icos.modules.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.auth.application.dto.LoginRequestDto;
import com.safo.icos.modules.auth.application.dto.RefreshResponseDto;
import com.safo.icos.modules.auth.application.dto.RegisterRequestDto;
import com.safo.icos.modules.auth.domain.exception.InvalidCredentialsException;
import com.safo.icos.modules.auth.domain.exception.UserAlreadyExistsException;
import com.safo.icos.modules.auth.domain.model.RefreshToken;
import com.safo.icos.modules.auth.domain.model.TokenValue;
import com.safo.icos.modules.auth.domain.repository.RefreshTokenRepository;
import com.safo.icos.modules.auth.domain.service.AuthDomainService;
import com.safo.icos.modules.auth.infrastructure.repository.AuthCredentialsJpaEntity;
import com.safo.icos.modules.auth.infrastructure.repository.AuthCredentialsJpaRepository;
import com.safo.icos.modules.invite.application.dto.InviteAcceptDto;
import com.safo.icos.modules.invite.domain.model.Invite;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.user.infrastructure.repository.PermissionJpaEntity;
import com.safo.icos.modules.user.infrastructure.repository.RoleJpaEntity;
import com.safo.icos.modules.user.infrastructure.repository.RoleJpaRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

  @Value("${app.auth.refresh-token-ttl:30d}")
  private Duration refreshTokenTtl;

  public AuthResponseDto register(RegisterRequestDto request, String deviceInfo) {
    log.debug("Registering new user with email: {}", request.getEmail());

    if (userProvider.existsByEmail(request.getEmail())) {
      throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
    }

    UserCreateDto createData = UserCreateDto.builder()
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .createdBy(null)
            .build();

    UserDto user = userProvider.createUser(createData);

    // Save password
    AuthCredentialsJpaEntity credentials = AuthCredentialsJpaEntity.builder()
            .userId(user.getId())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();
    credentialsRepository.save(credentials);

    // Create company and membership
    CompanyDto company = companyService.createCompanyInternal(request.getCompanyName(), null);

    UUID ownerRoleId = roleJpaRepository.findByName("OWNER")
            .map(RoleJpaEntity::getId)
            .orElseThrow(() -> new RuntimeException("Role OWNER not found"));

    MembershipDto membership = userProvider.createMembership(user.getId(), company.getId(), ownerRoleId, null, null);

    UserDto userWithCompany = UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .companyId(membership.getCompanyId().toString())
            .role(membership.getRole())
            .departmentId(membership.getDepartmentId())
            .permissions(roleJpaRepository.findById(membership.getRoleId())
                    .map(role -> role.getPermissions().stream()
                            .map(PermissionJpaEntity::getName)
                            .toList())
                    .orElse(List.of()))
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();

    RefreshToken refreshToken = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(refreshToken);

    AuthTokenDto tokens = authTokenProvider.generateTokens(
            user.getId(),
            membership.getCompanyId()
    );

    return AuthResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(tokens.getRefreshToken())
            .tokenType(tokens.getTokenType())
            .expiresIn(tokens.getExpiresIn())
            .user(userWithCompany)
            .build();
  }

  public AuthResponseDto login(LoginRequestDto request, String deviceInfo) {
    log.debug("Login attempt for email: {}", request.getEmail());

    UserDto user = userProvider.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

    AuthCredentialsJpaEntity credentials = credentialsRepository.findById(user.getId())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

    if (!passwordEncoder.matches(request.getPassword(), credentials.getPasswordHash())) {
      throw new InvalidCredentialsException("Invalid email or password");
    }

    MembershipDto membership = userProvider.findFirstActiveMembershipByUserId(user.getId())
            .orElseThrow(() -> new BusinessException(
                    "User does not have an active membership",
                    UserErrorCode.USER_NOT_ACTIVE));

    RefreshToken refreshToken = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(refreshToken);

    AuthTokenDto tokens = authTokenProvider.generateTokens(
            user.getId(),
            membership.getCompanyId()
    );

    UserDto userWithCompany = UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .companyId(membership.getCompanyId().toString())
            .role(membership.getRole())
            .departmentId(membership.getDepartmentId())
            .permissions(roleJpaRepository.findById(membership.getRoleId())
                    .map(role -> role.getPermissions().stream()
                            .map(PermissionJpaEntity::getName)
                            .toList())
                    .orElse(List.of()))
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();

    return AuthResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(tokens.getRefreshToken())
            .tokenType(tokens.getTokenType())
            .expiresIn(tokens.getExpiresIn())
            .user(userWithCompany)
            .build();
  }

  public RefreshResponseDto refresh(String refreshTokenValue, String deviceInfo) {
    log.debug("Refreshing token");

    RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(new TokenValue(refreshTokenValue))
            .orElseThrow(() -> new BusinessException("Invalid refresh token", AuthErrorCode.TOKEN_NOT_FOUND));

    authDomainService.validateRefreshToken(refreshToken);

    // Rotation: revoke old token
    RefreshToken revoked = refreshToken.revoke();
    refreshTokenRepository.save(revoked);

    // Get membership and user details
    MembershipDto membership = userProvider.findMembershipById(refreshToken.getMembershipId())
            .orElseThrow(() -> new BusinessException("Membership not found", UserErrorCode.USER_NOT_FOUND));

    UserDto user = userProvider.findById(membership.getUserId())
            .orElseThrow(() -> new BusinessException("User not found", UserErrorCode.USER_NOT_FOUND));

    // Create new refresh token
    RefreshToken newToken = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(newToken);

    // Generate new JWT
    AuthTokenDto tokens = authTokenProvider.generateTokens(
            user.getId(),
            membership.getCompanyId()
    );

    return RefreshResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(tokens.getRefreshToken())
            .expiresIn(tokens.getExpiresIn())
            .build();
  }

  public void logout(String refreshTokenValue) {
    refreshTokenRepository.findByTokenHash(new TokenValue(refreshTokenValue))
            .ifPresent(token -> {
              RefreshToken revoked = token.revoke();
              refreshTokenRepository.save(revoked);
            });
  }

  public AuthResponseDto registerFromInvite(Invite invite, InviteAcceptDto request, String deviceInfo) {
    log.debug("Registering user from invite for email: {}", invite.getEmail());

    if (userProvider.existsByEmail(invite.getEmail())) {
      throw new UserAlreadyExistsException("User with email " + invite.getEmail() + " already exists");
    }

    UserCreateDto createData = UserCreateDto.builder()
            .email(invite.getEmail())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .createdBy(invite.getInvitedById())
            .build();

    UserDto user = userProvider.createUser(createData);

    // Save password
    AuthCredentialsJpaEntity credentials = AuthCredentialsJpaEntity.builder()
            .userId(user.getId())
            .passwordHash(passwordEncoder.encode(request.password()))
            .build();
    credentialsRepository.save(credentials);

    // Create membership from invite
    UUID roleId = roleJpaRepository.findByName(invite.getRole().name())
            .map(RoleJpaEntity::getId)
            .orElseThrow(() -> new RuntimeException("Role " + invite.getRole().name() + " not found"));

    MembershipDto membership = userProvider.createMembership(
            user.getId(),
            invite.getCompanyId(),
            roleId,
            invite.getDepartmentId(),
            invite.getManagerId());

    UserDto userWithCompany = UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .companyId(membership.getCompanyId().toString())
            .role(membership.getRole())
            .departmentId(membership.getDepartmentId())
            .permissions(roleJpaRepository.findById(membership.getRoleId())
                    .map(role -> role.getPermissions().stream()
                            .map(PermissionJpaEntity::getName)
                            .toList())
                    .orElse(List.of()))
            .status(user.getStatus())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();

    RefreshToken refreshToken = authDomainService.createRefreshToken(
            membership.getId(), deviceInfo, refreshTokenTtl);
    refreshTokenRepository.save(refreshToken);

    AuthTokenDto tokens = authTokenProvider.generateTokens(
            user.getId(),
            membership.getCompanyId()
    );

    return AuthResponseDto.builder()
            .accessToken(tokens.getAccessToken())
            .refreshToken(tokens.getRefreshToken())
            .tokenType(tokens.getTokenType())
            .expiresIn(tokens.getExpiresIn())
            .user(userWithCompany)
            .build();
  }
}
