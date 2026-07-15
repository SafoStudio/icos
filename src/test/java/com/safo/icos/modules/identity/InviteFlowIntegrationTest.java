package com.safo.icos.modules.identity;

import com.safo.icos.modules.identity.auth.api.AuthTokenProvider;
import com.safo.icos.modules.identity.auth.application.AuthService;
import com.safo.icos.modules.identity.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.identity.invite.application.InviteService;
import com.safo.icos.modules.identity.invite.application.dto.InviteAcceptDto;
import com.safo.icos.modules.identity.invite.application.dto.InviteRequestDto;
import com.safo.icos.modules.identity.invite.application.dto.InviteResponseDto;
import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.identity.invite.infrastructure.email.EmailService;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.org.domain.model.enums.RoleName;
import com.safo.icos.security.context.IcosUserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration test for the full invite-to-registration flow.
 * Verifies that an invited user can be created, receive an email,
 * and complete registration with a valid access token and membership.
 * <p>
 * External services (EmailService, CompanyService, DepartmentService)
 * are mocked to avoid dependencies on real infrastructure.
 * <p>
 * Uses existing seed data IDs from the database.
 */
@SpringBootTest
@Transactional
class InviteFlowIntegrationTest {

  @Autowired
  private InviteService inviteService;
  @Autowired
  private AuthService authService;
  @Autowired
  private InviteRepository inviteRepository;
  @Autowired
  private UserProvider userProvider;
  @Autowired
  private AuthTokenProvider authTokenProvider;

  @MockitoBean
  private EmailService emailService;
  @MockitoBean
  private CompanyService companyService;
  @MockitoBean
  private DepartmentService departmentService;

  // Real IDs from seed data (companies.json, departments.json, users.json)
  private static final UUID COMPANY_ID = UUID.fromString("e4b10b0e-3fa1-4a4e-98ee-5fb9d2a3ea61");
  private static final UUID DEPARTMENT_ID = UUID.fromString("a0f7e1b5-ec6e-4b68-b78f-ef816a3a936a");
  private static final UUID ADMIN_USER_ID = UUID.fromString("7a67f6e0-2f98-4680-bc9b-efb2b7ea672a");

  private static final String INVITEE_EMAIL = "invitee@test.com";
  private static final String ADMIN_EMAIL = "admin@test.com";
  private static final String DEVICE = "integration-test";

  /**
   * Sets up a mock ADMIN principal in the security context
   * and configures mocked services to return stub data.
   */
  @BeforeEach
  void setUp() {
    IcosUserPrincipal adminPrincipal = IcosUserPrincipal.builder()
            .userId(ADMIN_USER_ID)
            .email(ADMIN_EMAIL)
            .membershipId(UUID.randomUUID())
            .companyId(COMPANY_ID)
            .roleName("ADMIN")
            .authorities(List.of())
            .build();

    SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(adminPrincipal, null, adminPrincipal.getAuthorities())
    );

    CompanyDto company = CompanyDto.builder()
            .id(COMPANY_ID)
            .name("Acme Corporation")
            .taxId("TAX-123456789")
            .build();

    DepartmentDto department = DepartmentDto.builder()
            .id(DEPARTMENT_ID)
            .name("Engineering")
            .companyId(COMPANY_ID)
            .build();

    when(companyService.getCompany(COMPANY_ID)).thenReturn(company);
    when(departmentService.getDepartmentsByCompany(COMPANY_ID)).thenReturn(List.of(department));
  }

  /**
   * Full happy-path flow:
   * <ol>
   *   <li>Admin creates an invite</li>
   *   <li>Invite is persisted with a hashed token</li>
   *   <li>Invitee registers via {@code registerFromInvite}</li>
   *   <li>Invite is marked ACCEPTED</li>
   *   <li>User and membership are created</li>
   *   <li>Access token is valid and contains correct claims</li>
   * </ol>
   */
  @Test
  void shouldCompleteFullInviteAndRegistrationFlow() {
    InviteRequestDto inviteRequest = new InviteRequestDto(
            INVITEE_EMAIL, RoleName.SPECIALIST, DEPARTMENT_ID, null
    );

    InviteResponseDto inviteResponse = inviteService.createInvite(COMPANY_ID, inviteRequest);

    assertThat(inviteResponse.email()).isEqualTo(INVITEE_EMAIL);
    assertThat(inviteResponse.status()).isEqualTo(InviteStatus.PENDING);
    assertThat(inviteResponse.role()).isEqualTo(RoleName.SPECIALIST);

    verify(emailService).sendInvitationEmail(any(), anyString(), anyString(), anyString());

    Invite savedInvite = inviteRepository.findById(inviteResponse.id()).orElseThrow();
    assertThat(savedInvite.getToken()).isNotBlank();
    assertThat(savedInvite.getToken()).isNotEqualTo("raw-token");

    InviteAcceptDto acceptDto = new InviteAcceptDto("Jane", "Smith", "securePass123");

    AuthResponseDto authResponse = authService.registerFromInvite(savedInvite, acceptDto, DEVICE);

    assertThat(authResponse.getAccessToken()).isNotBlank();
    assertThat(authResponse.getRefreshToken()).isNotBlank();
    assertThat(authResponse.getUser().getEmail()).isEqualTo(INVITEE_EMAIL);
    assertThat(authResponse.getUser().getFirstName()).isEqualTo("Jane");
    assertThat(authResponse.getUser().getLastName()).isEqualTo("Smith");
    assertThat(authResponse.getUser().getRole()).isEqualTo("SPECIALIST");

    Invite acceptedInvite = inviteRepository.findById(inviteResponse.id()).orElseThrow();
    assertThat(acceptedInvite.getStatus()).isEqualTo(InviteStatus.ACCEPTED);

    UserDto createdUser = userProvider.findByEmail(INVITEE_EMAIL).orElseThrow();
    assertThat(createdUser.getId()).isEqualTo(authResponse.getUser().getId());
    assertThat(createdUser.getStatus()).isEqualTo("ACTIVE");

    MembershipDto membership = userProvider.findFirstActiveMembershipByUserId(createdUser.getId()).orElseThrow();
    assertThat(membership.getCompanyId()).isEqualTo(COMPANY_ID);
    assertThat(membership.isActive()).isTrue();

    assertThat(authTokenProvider.validateToken(authResponse.getAccessToken())).isTrue();

    String extractedUserId = authTokenProvider.extractUserId(authResponse.getAccessToken());
    assertThat(extractedUserId).isEqualTo(authResponse.getUser().getId().toString());

    String extractedTenantId = authTokenProvider.extractTenantId(authResponse.getAccessToken());
    assertThat(extractedTenantId).isEqualTo(COMPANY_ID.toString());
  }
}
