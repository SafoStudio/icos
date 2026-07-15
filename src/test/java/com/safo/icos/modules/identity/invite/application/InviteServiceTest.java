package com.safo.icos.modules.identity.invite.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.modules.identity.invite.application.dto.InviteRequestDto;
import com.safo.icos.modules.identity.invite.application.dto.InviteResponseDto;
import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.identity.invite.infrastructure.email.EmailService;
import com.safo.icos.modules.identity.user.application.service.UserService;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.domain.model.enums.RoleName;
import com.safo.icos.security.context.IcosUserPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;

@ExtendWith(MockitoExtension.class)
class InviteServiceTest {

  @Mock private InviteRepository repository;
  @Mock private EmailService emailService;
  @Mock private UserService userService;
  @Mock private CompanyService companyService;
  @Mock private DepartmentService departmentService;
  @Mock private SecurityContext securityContext;
  @Mock private Authentication authentication;

  private MockedStatic<SecurityContextHolder> securityMock;

  private static final UUID COMPANY_ID = UUID.randomUUID();
  private static final UUID INVITE_ID = UUID.randomUUID();
  private static final UUID DEPARTMENT_ID = UUID.randomUUID();
  private static final UUID ADMIN_USER_ID = UUID.randomUUID();
  private static final String EMAIL = "invited@example.com";

  @InjectMocks
  private InviteService inviteService;

  @BeforeEach
  void setUp() {
    securityMock = Mockito.mockStatic(SecurityContextHolder.class);
  }

  @AfterEach
  void tearDown() {
    if (securityMock != null) {
      securityMock.close();
    }
  }

  private void mockAdminPrincipal() {
    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(ADMIN_USER_ID)
            .email("admin@example.com")
            .membershipId(UUID.randomUUID())
            .companyId(COMPANY_ID)
            .roleName("ADMIN")
            .departmentId(null)
            .departmentName(null)
            .authorities(List.of())
            .build();

    securityMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(principal);
  }

  // ==================== createInvite ====================

  @Test
  void shouldCreateInviteSuccessfully() {
    mockAdminPrincipal();

    InviteRequestDto dto = new InviteRequestDto(EMAIL, RoleName.SPECIALIST, DEPARTMENT_ID, null);
    CompanyDto company = companyDto();
    DepartmentDto department = departmentDto();
    Invite savedInvite = pendingInvite();

    when(repository.existsByEmail(EMAIL)).thenReturn(false);
    when(repository.save(any())).thenReturn(savedInvite);
    when(companyService.getCompany(COMPANY_ID)).thenReturn(company);
    when(departmentService.getDepartmentsByCompany(COMPANY_ID)).thenReturn(List.of(department));

    InviteResponseDto result = inviteService.createInvite(COMPANY_ID, dto);

    assertThat(result.email()).isEqualTo(EMAIL);
    assertThat(result.status()).isEqualTo(InviteStatus.PENDING);
    assertThat(result.companyName()).isEqualTo("TestCo");
    assertThat(result.departmentName()).isEqualTo("Engineering");

    verify(repository).existsByEmail(EMAIL);
    verify(repository).save(any());
    verify(emailService).sendInvitationEmail(any(), eq("TestCo"), eq("Engineering"), eq("None"));
  }

  @Test
  void shouldThrowWhenEmailAlreadyExistsOnCreateInvite() {
    mockAdminPrincipal();

    InviteRequestDto dto = new InviteRequestDto(EMAIL, RoleName.SPECIALIST, DEPARTMENT_ID, null);

    when(repository.existsByEmail(EMAIL)).thenReturn(true);

    assertThatThrownBy(() -> inviteService.createInvite(COMPANY_ID, dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already exists");

    verify(repository, never()).save(any());
  }

  // ==================== getInviteByToken ====================

  @Test
  void shouldReturnInviteByToken() {
    Invite invite = pendingInvite();
    CompanyDto company = companyDto();
    DepartmentDto department = departmentDto();

    when(repository.findByToken(any())).thenReturn(Optional.of(invite));
    when(companyService.getCompany(COMPANY_ID)).thenReturn(company);
    when(departmentService.getDepartmentsByCompany(COMPANY_ID)).thenReturn(List.of(department));

    InviteResponseDto result = inviteService.getInviteByToken("raw-token");

    assertThat(result.email()).isEqualTo(EMAIL);
    assertThat(result.status()).isEqualTo(InviteStatus.PENDING);
    assertThat(result.companyName()).isEqualTo("TestCo");
    assertThat(result.departmentName()).isEqualTo("Engineering");
  }

  @Test
  void shouldThrowWhenTokenNotFound() {
    when(repository.findByToken(any())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> inviteService.getInviteByToken("bad-token"))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Invite not found");
  }

  @Test
  void shouldThrowWhenInviteExpired() {
    Invite expiredInvite = expiredInvite();
    when(repository.findByToken(any())).thenReturn(Optional.of(expiredInvite));

    assertThatThrownBy(() -> inviteService.getInviteByToken("raw-token"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("expired");
  }

  @Test
  void shouldThrowWhenInviteNotPending() {
    Invite acceptedInvite = acceptedInvite();
    when(repository.findByToken(any())).thenReturn(Optional.of(acceptedInvite));

    assertThatThrownBy(() -> inviteService.getInviteByToken("raw-token"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("no longer pending");
  }

  // ==================== cancelInvite ====================

  @Test
  void shouldCancelInviteSuccessfully() {
    mockAdminPrincipal();

    Invite invite = pendingInvite();

    when(repository.findById(INVITE_ID)).thenReturn(Optional.of(invite));
    when(repository.save(any())).thenReturn(invite);

    inviteService.cancelInvite(INVITE_ID);

    verify(repository).save(any());
  }

  @Test
  void shouldThrowWhenInviteNotFoundOnCancel() {
    mockAdminPrincipal();

    when(repository.findById(INVITE_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> inviteService.cancelInvite(INVITE_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Invite not found");

    verify(repository, never()).save(any());
  }

  // ==================== resendInvite ====================

  @Test
  void shouldResendInviteSuccessfully() {
    mockAdminPrincipal();

    Invite existingInvite = pendingInvite();
    CompanyDto company = companyDto();
    DepartmentDto department = departmentDto();

    when(repository.findById(INVITE_ID)).thenReturn(Optional.of(existingInvite));
    when(repository.save(any())).thenReturn(existingInvite);
    when(companyService.getCompany(COMPANY_ID)).thenReturn(company);
    when(departmentService.getDepartmentsByCompany(COMPANY_ID)).thenReturn(List.of(department));

    InviteResponseDto result = inviteService.resendInvite(INVITE_ID);

    assertThat(result.email()).isEqualTo(EMAIL);
    assertThat(result.status()).isEqualTo(InviteStatus.PENDING);

    verify(repository).save(any());
    verify(emailService).sendInvitationEmail(any(), any(), any(), any());
  }

  @Test
  void shouldThrowWhenResendAcceptedInvite() {
    mockAdminPrincipal();

    Invite acceptedInvite = acceptedInvite();

    when(repository.findById(INVITE_ID)).thenReturn(Optional.of(acceptedInvite));

    assertThatThrownBy(() -> inviteService.resendInvite(INVITE_ID))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already accepted");

    verify(repository, never()).save(any());
  }

  @Test
  void shouldThrowWhenInviteNotFoundOnResend() {
    mockAdminPrincipal();

    when(repository.findById(INVITE_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> inviteService.resendInvite(INVITE_ID))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Invite not found");
  }

  // ==================== helpers ====================

  private Invite pendingInvite() {
    return Invite.builder()
            .id(INVITE_ID)
            .email(EMAIL)
            .role(RoleName.SPECIALIST)
            .companyId(COMPANY_ID)
            .departmentId(DEPARTMENT_ID)
            .managerId(null)
            .invitedById(ADMIN_USER_ID)
            .token("hashed-token")
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(OffsetDateTime.now())
            .build();
  }

  private Invite expiredInvite() {
    return Invite.builder()
            .id(INVITE_ID)
            .email(EMAIL)
            .role(RoleName.SPECIALIST)
            .companyId(COMPANY_ID)
            .departmentId(DEPARTMENT_ID)
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().minusDays(1))
            .createdAt(OffsetDateTime.now().minusDays(8))
            .build();
  }

  private Invite acceptedInvite() {
    return Invite.builder()
            .id(INVITE_ID)
            .email(EMAIL)
            .role(RoleName.SPECIALIST)
            .companyId(COMPANY_ID)
            .departmentId(DEPARTMENT_ID)
            .status(InviteStatus.ACCEPTED)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(OffsetDateTime.now())
            .build();
  }

  private CompanyDto companyDto() {
    return CompanyDto.builder()
            .id(COMPANY_ID)
            .name("TestCo")
            .taxId("123456789")
            .build();
  }

  private DepartmentDto departmentDto() {
    return DepartmentDto.builder()
            .id(DEPARTMENT_ID)
            .name("Engineering")
            .companyId(COMPANY_ID)
            .build();
  }
}
