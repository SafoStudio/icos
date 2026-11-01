package com.safo.icos.modules.identity.auth.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.auth.api.AuthTokenProvider;
import com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto;
import com.safo.icos.modules.identity.auth.application.dto.*;
import com.safo.icos.modules.identity.auth.domain.exception.InvalidCredentialsException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenExpiredException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenRevokedException;
import com.safo.icos.modules.identity.auth.domain.exception.UserAlreadyExistsException;
import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;
import com.safo.icos.modules.identity.auth.domain.model.RefreshTokenId;
import com.safo.icos.modules.identity.auth.domain.model.TokenValue;
import com.safo.icos.modules.identity.auth.domain.repository.RefreshTokenRepository;
import com.safo.icos.modules.identity.auth.domain.service.AuthDomainService;
import com.safo.icos.modules.identity.auth.domain.service.RefreshTokenCreationResult;
import com.safo.icos.modules.identity.auth.infrastructure.repository.entity.AuthCredentialsJpaEntity;
import com.safo.icos.modules.identity.auth.infrastructure.repository.jpa.AuthCredentialsJpaRepository;
import com.safo.icos.modules.identity.invite.application.dto.InviteAcceptDto;
import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.PermissionJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.RoleJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.RoleJpaRepository;
import com.safo.icos.modules.org.domain.model.enums.RoleName;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.PermissionJpaRepository;
import com.safo.icos.modules.org.infrastructure.repository.jpa.DepartmentPermissionJpaRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserProvider userProvider;
  @Mock private AuthTokenProvider authTokenProvider;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private AuthDomainService authDomainService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthCredentialsJpaRepository credentialsRepository;
  @Mock private CompanyService companyService;
  @Mock private RoleJpaRepository roleJpaRepository;
  @Mock private PermissionJpaRepository permissionJpaRepository;
  @Mock private DepartmentPermissionJpaRepository departmentPermissionJpaRepository;
  @Mock private InviteRepository inviteRepository;

  @InjectMocks
  private AuthService authService;

  private static final String EMAIL = "test@example.com";
  private static final String PASSWORD = "password123";
  private static final String HASHED_PASSWORD = "$2a$12$hashedpassword";
  private static final String DEVICE = "test-device";
  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID MEMBERSHIP_ID = UUID.randomUUID();
  private static final UUID OWNER_ROLE_ID = UUID.randomUUID();
  private static final UUID SPECIALIST_ROLE_ID = UUID.randomUUID();
  private static final UUID DEPARTMENT_ID = UUID.randomUUID();
  private static final Duration REFRESH_TTL = Duration.ofDays(30);

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(authService, "refreshTokenTtl", REFRESH_TTL);
  }

  // ==================== register ====================

  @Test
  void shouldRegisterNewUserSuccessfully() {
    RegisterRequestDto request = registerRequest();
    UserDto createdUser = userDto();
    CompanyDto company = CompanyDto.builder()
            .id(COMPANY_ID).name("TestCo").taxId("123456789").build();
    MembershipDto membership = ownerMembershipDto();
    RefreshTokenCreationResult rtResult = refreshTokenCreationResult();
    AuthTokenDto tokens = authTokenDto();
    RoleJpaEntity ownerRole = ownerRoleEntity();

    when(userProvider.existsByEmail(EMAIL)).thenReturn(false);
    when(userProvider.createUser(any())).thenReturn(createdUser);
    when(companyService.createCompanyInternal(eq("TestCo"), isNull())).thenReturn(company);
    when(roleJpaRepository.findByName("OWNER")).thenReturn(Optional.of(ownerRole));
    when(userProvider.createMembership(USER_ID, COMPANY_ID, OWNER_ROLE_ID, null, null))
            .thenReturn(membership);
    when(roleJpaRepository.findById(OWNER_ROLE_ID)).thenReturn(Optional.of(ownerRole));
    when(authDomainService.createRefreshToken(MEMBERSHIP_ID, DEVICE, REFRESH_TTL))
            .thenReturn(rtResult);
    when(refreshTokenRepository.save(rtResult.entity())).thenReturn(rtResult.entity());
    when(authTokenProvider.generateAccessToken(USER_ID, COMPANY_ID)).thenReturn(tokens);
    when(permissionJpaRepository.findAll()).thenReturn(List.of());

    AuthResponseDto result = authService.register(request, DEVICE);

    assertThat(result.getAccessToken()).isEqualTo(tokens.getAccessToken());
    assertThat(result.getRefreshToken()).isEqualTo(rtResult.rawToken());
    assertThat(result.getTokenType()).isEqualTo(tokens.getTokenType());
    assertThat(result.getExpiresIn()).isEqualTo(tokens.getExpiresIn());
    assertThat(result.getUser().getId()).isEqualTo(USER_ID);
    assertThat(result.getUser().getCompanyId()).isEqualTo(COMPANY_ID.toString());
    assertThat(result.getUser().getRole()).isEqualTo("OWNER");

    verify(userProvider).existsByEmail(EMAIL);
    verify(userProvider).createUser(any());
    verify(companyService).createCompanyInternal(eq("TestCo"), isNull());
    verify(authDomainService).createRefreshToken(MEMBERSHIP_ID, DEVICE, REFRESH_TTL);
    verify(authTokenProvider).generateAccessToken(USER_ID, COMPANY_ID);
  }

  @Test
  void shouldThrowWhenEmailAlreadyExistsOnRegister() {
    RegisterRequestDto request = registerRequest();
    when(userProvider.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request, DEVICE))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("already exists");

    verify(userProvider, never()).createUser(any());
  }

  // ==================== login ====================

  @Test
  void shouldLoginSuccessfully() {
    LoginRequestDto request = loginRequest();
    UserDto user = userDto();
    AuthCredentialsJpaEntity credentials = authCredentials();
    MembershipDto membership = ownerMembershipDto();
    RefreshTokenCreationResult rtResult = refreshTokenCreationResult();
    AuthTokenDto tokens = authTokenDto();
    RoleJpaEntity ownerRole = ownerRoleEntity();

    when(userProvider.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(credentialsRepository.findById(USER_ID)).thenReturn(Optional.of(credentials));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
    when(userProvider.findFirstActiveMembershipByUserId(USER_ID)).thenReturn(Optional.of(membership));
    when(roleJpaRepository.findById(OWNER_ROLE_ID)).thenReturn(Optional.of(ownerRole));
    when(authDomainService.createRefreshToken(MEMBERSHIP_ID, DEVICE, REFRESH_TTL))
            .thenReturn(rtResult);
    when(refreshTokenRepository.save(rtResult.entity())).thenReturn(rtResult.entity());
    when(authTokenProvider.generateAccessToken(USER_ID, COMPANY_ID)).thenReturn(tokens);
    when(permissionJpaRepository.findAll()).thenReturn(List.of());

    AuthResponseDto result = authService.login(request, DEVICE);

    assertThat(result.getAccessToken()).isEqualTo(tokens.getAccessToken());
    assertThat(result.getRefreshToken()).isEqualTo(rtResult.rawToken());
    assertThat(result.getUser().getEmail()).isEqualTo(EMAIL);
  }

  @Test
  void shouldThrowWhenUserNotFoundOnLogin() {
    LoginRequestDto request = loginRequest();
    when(userProvider.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request, DEVICE))
            .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void shouldThrowWhenInvalidPasswordOnLogin() {
    LoginRequestDto request = loginRequest();
    UserDto user = userDto();
    AuthCredentialsJpaEntity credentials = authCredentials();

    when(userProvider.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(credentialsRepository.findById(USER_ID)).thenReturn(Optional.of(credentials));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(false);

    assertThatThrownBy(() -> authService.login(request, DEVICE))
            .isInstanceOf(InvalidCredentialsException.class);
  }

  @Test
  void shouldThrowWhenNoActiveMembershipOnLogin() {
    LoginRequestDto request = loginRequest();
    UserDto user = userDto();
    AuthCredentialsJpaEntity credentials = authCredentials();

    when(userProvider.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(credentialsRepository.findById(USER_ID)).thenReturn(Optional.of(credentials));
    when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);
    when(userProvider.findFirstActiveMembershipByUserId(USER_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request, DEVICE))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("active membership");
  }

  // ==================== refresh ====================

  @Test
  void shouldRefreshTokenSuccessfully() {
    String rawToken = TokenValue.generateRaw();
    RefreshToken oldToken = validRefreshToken(rawToken);
    RefreshTokenCreationResult newRtResult = refreshTokenCreationResult();
    MembershipDto membership = ownerMembershipDto();
    UserDto user = userDto();
    AuthTokenDto tokens = authTokenDto();

    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(oldToken));
    when(refreshTokenRepository.save(any())).thenReturn(oldToken.revoke(), newRtResult.entity());
    when(userProvider.findMembershipById(MEMBERSHIP_ID)).thenReturn(Optional.of(membership));
    when(userProvider.findById(USER_ID)).thenReturn(Optional.of(user));
    when(authDomainService.createRefreshToken(MEMBERSHIP_ID, DEVICE, REFRESH_TTL))
            .thenReturn(newRtResult);
    when(authTokenProvider.generateAccessToken(USER_ID, COMPANY_ID)).thenReturn(tokens);

    RefreshResponseDto result = authService.refresh(rawToken, DEVICE);

    assertThat(result.getAccessToken()).isEqualTo(tokens.getAccessToken());
    assertThat(result.getRefreshToken()).isEqualTo(newRtResult.rawToken());
    assertThat(result.getExpiresIn()).isEqualTo(tokens.getExpiresIn());

    verify(refreshTokenRepository, times(2)).save(any());
  }

  @Test
  void shouldThrowWhenRefreshTokenNotFound() {
    String rawToken = TokenValue.generateRaw();
    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.refresh(rawToken, DEVICE))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Invalid refresh token");
  }

  @Test
  void shouldThrowWhenRefreshTokenExpired() {
    String rawToken = TokenValue.generateRaw();
    RefreshToken expiredToken = validRefreshToken(rawToken);

    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(expiredToken));
    doThrow(new TokenExpiredException("Refresh token has expired"))
            .when(authDomainService).validateRefreshToken(expiredToken);

    assertThatThrownBy(() -> authService.refresh(rawToken, DEVICE))
            .isInstanceOf(TokenExpiredException.class);
  }

  @Test
  void shouldThrowWhenRefreshTokenRevoked() {
    String rawToken = TokenValue.generateRaw();
    RefreshToken revokedToken = validRefreshToken(rawToken);

    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(revokedToken));
    doThrow(new TokenRevokedException("Refresh token has been revoked"))
            .when(authDomainService).validateRefreshToken(revokedToken);

    assertThatThrownBy(() -> authService.refresh(rawToken, DEVICE))
            .isInstanceOf(TokenRevokedException.class);
  }

  // ==================== logout ====================

  @Test
  void shouldRevokeTokenOnLogout() {
    String rawToken = TokenValue.generateRaw();
    RefreshToken token = validRefreshToken(rawToken);

    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.of(token));
    when(refreshTokenRepository.save(any())).thenReturn(token.revoke());

    authService.logout(rawToken);

    verify(refreshTokenRepository).save(any());
  }

  @Test
  void shouldNotFailWhenTokenNotFoundOnLogout() {
    when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

    authService.logout(TokenValue.generateRaw());

    verify(refreshTokenRepository, never()).save(any());
  }

  // ==================== registerFromInvite ====================

  @Test
  void shouldRegisterFromInviteSuccessfully() {
    Invite invite = testInvite();
    InviteAcceptDto acceptDto = inviteAcceptDto();
    UserDto createdUser = userDto();
    RoleJpaEntity specialistRole = specialistRoleEntity();
    MembershipDto membership = specialistMembershipDto();
    RefreshTokenCreationResult rtResult = refreshTokenCreationResult();
    AuthTokenDto tokens = authTokenDto();

    when(userProvider.existsByEmail(invite.getEmail())).thenReturn(false);
    when(userProvider.createUser(any())).thenReturn(createdUser);
    when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
    when(roleJpaRepository.findByName("SPECIALIST")).thenReturn(Optional.of(specialistRole));
    when(userProvider.createMembership(USER_ID, COMPANY_ID, SPECIALIST_ROLE_ID, DEPARTMENT_ID, null))
            .thenReturn(membership);
    when(roleJpaRepository.findById(SPECIALIST_ROLE_ID)).thenReturn(Optional.of(specialistRole));
    when(authDomainService.createRefreshToken(MEMBERSHIP_ID, DEVICE, REFRESH_TTL))
            .thenReturn(rtResult);
    when(refreshTokenRepository.save(rtResult.entity())).thenReturn(rtResult.entity());
    when(authTokenProvider.generateAccessToken(USER_ID, COMPANY_ID)).thenReturn(tokens);
    when(departmentPermissionJpaRepository
            .findByDepartmentIdAndRoleName(DEPARTMENT_ID, "SPECIALIST"))
            .thenReturn(List.of());
    when(inviteRepository.save(any(Invite.class))).thenAnswer(inv -> inv.getArgument(0));

    AuthResponseDto result = authService.registerFromInvite(invite, acceptDto, DEVICE);

    assertThat(result.getAccessToken()).isEqualTo(tokens.getAccessToken());
    assertThat(result.getRefreshToken()).isEqualTo(rtResult.rawToken());
    assertThat(result.getTokenType()).isEqualTo(tokens.getTokenType());
    assertThat(result.getExpiresIn()).isEqualTo(tokens.getExpiresIn());
    assertThat(result.getUser().getId()).isEqualTo(USER_ID);
    assertThat(result.getUser().getRole()).isEqualTo("SPECIALIST");

    verify(userProvider).existsByEmail(invite.getEmail());
    verify(userProvider).createUser(any());
    verify(roleJpaRepository).findByName("SPECIALIST");
    verify(authDomainService).createRefreshToken(MEMBERSHIP_ID, DEVICE, REFRESH_TTL);
    verify(refreshTokenRepository).save(any());
    verify(authTokenProvider).generateAccessToken(USER_ID, COMPANY_ID);
    verify(inviteRepository).save(any(Invite.class));
  }

  @Test
  void shouldThrowWhenEmailAlreadyExistsOnRegisterFromInvite() {
    Invite invite = testInvite();
    InviteAcceptDto acceptDto = inviteAcceptDto();

    when(userProvider.existsByEmail(invite.getEmail())).thenReturn(true);

    assertThatThrownBy(() -> authService.registerFromInvite(invite, acceptDto, DEVICE))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("already exists");

    verify(userProvider).existsByEmail(invite.getEmail());
    verify(userProvider, never()).createUser(any());
  }

  @Test
  void shouldThrowWhenRoleNotFoundOnRegisterFromInvite() {
    Invite invite = testInvite();
    InviteAcceptDto acceptDto = inviteAcceptDto();
    UserDto createdUser = userDto();

    when(userProvider.existsByEmail(invite.getEmail())).thenReturn(false);
    when(userProvider.createUser(any())).thenReturn(createdUser);
    when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
    when(roleJpaRepository.findByName("SPECIALIST")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.registerFromInvite(invite, acceptDto, DEVICE))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
  }

  // ==================== helpers ====================

  private RegisterRequestDto registerRequest() {
    return RegisterRequestDto.builder()
            .email(EMAIL).password(PASSWORD)
            .firstName("John").lastName("Doe").companyName("TestCo").build();
  }

  private LoginRequestDto loginRequest() {
    return LoginRequestDto.builder().email(EMAIL).password(PASSWORD).build();
  }

  private UserDto userDto() {
    return UserDto.builder()
            .id(USER_ID).email(EMAIL).firstName("John").lastName("Doe")
            .status("ACTIVE").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
  }

  private MembershipDto ownerMembershipDto() {
    return MembershipDto.builder()
            .id(MEMBERSHIP_ID).userId(USER_ID).companyId(COMPANY_ID)
            .role("OWNER").roleId(OWNER_ROLE_ID).active(true).build();
  }

  private MembershipDto specialistMembershipDto() {
    return MembershipDto.builder()
            .id(MEMBERSHIP_ID).userId(USER_ID).companyId(COMPANY_ID)
            .role("SPECIALIST").roleId(SPECIALIST_ROLE_ID)
            .departmentId(DEPARTMENT_ID).active(true).build();
  }

  private AuthCredentialsJpaEntity authCredentials() {
    return AuthCredentialsJpaEntity.builder()
            .userId(USER_ID).passwordHash(HASHED_PASSWORD).build();
  }

  private AuthTokenDto authTokenDto() {
    return AuthTokenDto.builder()
            .accessToken("access-jwt-token").tokenType("Bearer").expiresIn(900).build();
  }

  private RefreshTokenCreationResult refreshTokenCreationResult() {
    String raw = TokenValue.generateRaw();
    RefreshToken entity = RefreshToken.create(
            MEMBERSHIP_ID, TokenValue.fromRaw(raw), DEVICE, Instant.now().plus(REFRESH_TTL));
    return new RefreshTokenCreationResult(entity, raw);
  }

  private RefreshToken validRefreshToken(String rawToken) {
    return RefreshToken.builder()
            .id(RefreshTokenId.generate()).membershipId(MEMBERSHIP_ID)
            .token(TokenValue.fromRaw(rawToken)).deviceInfo(DEVICE)
            .createdAt(Instant.now()).expiresAt(Instant.now().plus(REFRESH_TTL))
            .revoked(false).revokedAt(null).build();
  }

  private RoleJpaEntity ownerRoleEntity() {
    return RoleJpaEntity.builder()
            .id(OWNER_ROLE_ID).name("OWNER")
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .permissions(Set.of(permission("user:read"), permission("user:edit")))
            .build();
  }

  private RoleJpaEntity specialistRoleEntity() {
    return RoleJpaEntity.builder()
            .id(SPECIALIST_ROLE_ID).name("SPECIALIST")
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .permissions(Set.of(permission("user:read")))
            .build();
  }

  private PermissionJpaEntity permission(String name) {
    return PermissionJpaEntity.builder()
            .id(UUID.randomUUID()).name(name).version(1)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
  }

  private Invite testInvite() {
    return Invite.builder()
            .id(UUID.randomUUID())
            .email("invited@example.com")
            .role(RoleName.SPECIALIST)
            .companyId(COMPANY_ID)
            .departmentId(DEPARTMENT_ID)
            .managerId(null)
            .invitedById(USER_ID)
            .token("hashed-token")
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(OffsetDateTime.now())
            .build();
  }

  private InviteAcceptDto inviteAcceptDto() {
    return new InviteAcceptDto("Jane", "Doe", "password123");
  }
}
