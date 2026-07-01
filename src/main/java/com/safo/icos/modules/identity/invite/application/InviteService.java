package com.safo.icos.modules.identity.invite.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.common.util.HashUtils;
import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;
import com.safo.icos.modules.identity.invite.application.dto.InviteRequestDto;
import com.safo.icos.modules.identity.invite.application.dto.InviteResponseDto;
import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.model.enums.InviteStatus;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.identity.invite.infrastructure.email.EmailService;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.identity.user.api.constant.UserErrorCode;
import com.safo.icos.modules.identity.user.application.service.UserService;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for invitation management.
 * Handles creating, viewing, resending, and cancelling invitations.
 * Only users with ADMIN, OWNER, or HR department membership
 * can manage invites.
 */
@Service
@RequiredArgsConstructor
public class InviteService {
  private static final String DEPT_NOT_FOUND_MSG = "Department not found";
  private static final String INVITE_NOT_FOUND_MSG = "Invite not found";
  private final InviteRepository repository;
  private final EmailService emailService;
  private final UserService userService;
  private final CompanyService companyService;
  private final DepartmentService departmentService;
  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * Extracts the currently authenticated principal from the security context.
   *
   * @return the current user's principal
   * @throws BusinessException if no authenticated user is found
   */
  private IcosUserPrincipal getPrincipal() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof IcosUserPrincipal principal)) {
      throw new BusinessException("User not authenticated", AuthErrorCode.TOKEN_NOT_FOUND);
    }
    return principal;
  }

  /**
   * Checks that the current user has permission to manage invites.
   * Allowed roles: ADMIN, OWNER, or any member of the HR department.
   *
   * @throws BusinessException if the user lacks permission
   */
  private void checkInvitePermissions() {
    IcosUserPrincipal principal = getPrincipal();

    boolean isPrivilegedRole = principal.getRoleName().equals("ADMIN") || principal.getRoleName().equals("OWNER");
    boolean isHrMember = principal.getDepartmentName() != null && principal.getDepartmentName().equalsIgnoreCase("HR");

    if (!isPrivilegedRole && !isHrMember) {
      throw new BusinessException(
              "Access denied: Only ADMIN, OWNER or HR department members can manage invites",
              UserErrorCode.USER_NOT_ACTIVE
      );
    }
  }

  /**
   * Creates a new invitation for the given company.
   * Generates a secure token, hashes it for storage, and sends
   * the raw token to the invitee via email.
   *
   * @param companyId the target company
   * @param dto       invitation details (email, role, department, manager)
   * @return the created invitation with resolved names
   * @throws BusinessException if an invite for this email already exists
   */
  @Transactional
  public InviteResponseDto createInvite(UUID companyId, InviteRequestDto dto) {
    checkInvitePermissions();

    if (repository.existsByEmail(dto.email())) {
      throw new BusinessException("An invitation for this email already exists", UserErrorCode.EMAIL_ALREADY_EXISTS);
    }

    IcosUserPrincipal principal = getPrincipal();

    String token = generateSafeToken();

    Invite invite = Invite.builder()
            .id(UUID.randomUUID())
            .email(dto.email())
            .role(dto.role())
            .companyId(companyId)
            .departmentId(dto.departmentId())
            .managerId(dto.managerId())
            .invitedById(principal.getUserId())
            .token(HashUtils.hash(token))
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(OffsetDateTime.now())
            .build();

    Invite saved = repository.save(invite);

    CompanyDto company = companyService.getCompany(companyId);
    DepartmentDto department = departmentService.getDepartmentsByCompany(companyId).stream()
            .filter(d -> d.getId().equals(dto.departmentId()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(DEPT_NOT_FOUND_MSG));

    String managerName = "None";
    if (dto.managerId() != null) {
      var manager = userService.getUserById(dto.managerId());
      managerName = manager.getFirstName() + " " + manager.getLastName();
    }

    emailService.sendInvitationEmail(saved, company.getName(), department.getName(), managerName);

    return toResponseDto(saved, company.getName(), department.getName(), managerName);
  }

  /**
   * Retrieves an invitation by its raw token.
   * The token is hashed before lookup. Validates that the invite
   * is still pending and not expired.
   *
   * @param token the raw token from the invitation link
   * @return the invitation details
   * @throws NotFoundException if the token does not match any invite
   * @throws BusinessException if the invite is not pending or has expired
   */
  public InviteResponseDto getInviteByToken(String token) {
    Invite invite = repository.findByToken(HashUtils.hash(token))
            .orElseThrow(() -> new NotFoundException(INVITE_NOT_FOUND_MSG));

    if (invite.isNotPending()) {
      throw new BusinessException("Invite is no longer pending", AuthErrorCode.TOKEN_REVOKED);
    }
    if (invite.isExpired()) {
      throw new BusinessException("Invite has expired", AuthErrorCode.TOKEN_EXPIRED);
    }

    CompanyDto company = companyService.getCompany(invite.getCompanyId());

    DepartmentDto department = departmentService.getDepartmentsByCompany(invite.getCompanyId()).stream()
            .filter(d -> d.getId().equals(invite.getDepartmentId()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(DEPT_NOT_FOUND_MSG));

    String managerName = "None";
    if (invite.getManagerId() != null) {
      var manager = userService.getUserById(invite.getManagerId());
      managerName = manager.getFirstName() + " " + manager.getLastName();
    }

    return toResponseDto(invite, company.getName(), department.getName(), managerName);
  }

  private InviteResponseDto toResponseDto(Invite invite, String companyName, String deptName, String managerName) {
    return new InviteResponseDto(
            invite.getId(),
            invite.getEmail(),
            invite.getRole(),
            invite.getCompanyId(),
            companyName,
            invite.getDepartmentId(),
            deptName,
            invite.getManagerId(),
            managerName,
            invite.getStatus(),
            invite.getExpiresAt()
    );
  }

  private String generateSafeToken() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }

  /**
   * Resends an existing invitation with a new token and expiration date.
   *
   * @param inviteId the ID of the invite to resend
   * @return the updated invitation
   * @throws NotFoundException if the invite does not exist
   * @throws BusinessException if the invite has already been accepted
   */
  @Transactional
  public InviteResponseDto resendInvite(UUID inviteId) {
    checkInvitePermissions();

    Invite invite = repository.findById(inviteId)
            .orElseThrow(() -> new NotFoundException(INVITE_NOT_FOUND_MSG));

    if (invite.getStatus() == InviteStatus.ACCEPTED) {
      throw new BusinessException("Cannot resend an already accepted invitation", AuthErrorCode.TOKEN_REVOKED);
    }

    String newToken = generateSafeToken();

    Invite updatedInvite = Invite.builder()
            .id(invite.getId())
            .email(invite.getEmail())
            .role(invite.getRole())
            .companyId(invite.getCompanyId())
            .departmentId(invite.getDepartmentId())
            .managerId(invite.getManagerId())
            .invitedById(invite.getInvitedById())
            .token(HashUtils.hash(newToken))
            .status(InviteStatus.PENDING)
            .expiresAt(OffsetDateTime.now().plusDays(7))
            .createdAt(invite.getCreatedAt())
            .updatedAt(OffsetDateTime.now())
            .build();

    Invite saved = repository.save(updatedInvite);

    CompanyDto company = companyService.getCompany(saved.getCompanyId());
    DepartmentDto department = departmentService.getDepartmentsByCompany(saved.getCompanyId()).stream()
            .filter(d -> d.getId().equals(saved.getDepartmentId()))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(DEPT_NOT_FOUND_MSG));

    String managerName = "None";
    if (saved.getManagerId() != null) {
      var manager = userService.getUserById(saved.getManagerId());
      managerName = manager.getFirstName() + " " + manager.getLastName();
    }

    emailService.sendInvitationEmail(saved, company.getName(), department.getName(), managerName);

    return toResponseDto(saved, company.getName(), department.getName(), managerName);
  }

  /**
   * Cancels a pending invitation.
   *
   * @param inviteId the ID of the invite to cancel
   * @throws NotFoundException if the invite does not exist
   */
  @Transactional
  public void cancelInvite(UUID inviteId) {
    checkInvitePermissions();
    Invite invite = repository.findById(inviteId)
            .orElseThrow(() -> new NotFoundException(INVITE_NOT_FOUND_MSG));
    invite.cancel();
    repository.save(invite);
  }
}
