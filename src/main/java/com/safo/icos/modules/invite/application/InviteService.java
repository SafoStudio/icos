package com.safo.icos.modules.invite.application;

import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.common.enums.InviteStatus;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.exception.NotFoundException;
import com.safo.icos.common.util.HashUtils;
import com.safo.icos.modules.invite.application.dto.InviteRequestDto;
import com.safo.icos.modules.invite.application.dto.InviteResponseDto;
import com.safo.icos.modules.invite.domain.model.Invite;
import com.safo.icos.modules.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.invite.infrastructure.email.EmailService;
import com.safo.icos.modules.org.application.CompanyService;
import com.safo.icos.modules.org.application.DepartmentService;
import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.application.dto.DepartmentDto;
import com.safo.icos.modules.user.application.UserService;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  private IcosUserPrincipal getPrincipal() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof IcosUserPrincipal principal)) {
      throw new BusinessException("User not authenticated", ErrorCode.TOKEN_NOT_FOUND);
    }
    return principal;
  }

  private void checkInvitePermissions() {
    IcosUserPrincipal principal = getPrincipal();

    boolean isPrivilegedRole = principal.getRoleName().equals("ADMIN") || principal.getRoleName().equals("OWNER");
    boolean isHrMember = principal.getDepartmentName() != null && principal.getDepartmentName().equalsIgnoreCase("HR");

    if (!isPrivilegedRole && !isHrMember) {
      throw new BusinessException(
              "Access denied: Only ADMIN, OWNER or HR department members can manage invites",
              ErrorCode.USER_NOT_ACTIVE
      );
    }
  }

  @Transactional
  public InviteResponseDto createInvite(UUID companyId, InviteRequestDto dto) {
    checkInvitePermissions();

    if (repository.existsByEmail(dto.email())) {
      throw new BusinessException("An invitation for this email already exists", ErrorCode.EMAIL_ALREADY_EXISTS);
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

  public InviteResponseDto getInviteByToken(String token) {
    Invite invite = repository.findByToken(HashUtils.hash(token))
            .orElseThrow(() -> new NotFoundException(INVITE_NOT_FOUND_MSG));

    if (invite.isNotPending()) {
      throw new BusinessException("Invite is no longer pending", ErrorCode.TOKEN_REVOKED);
    }
    if (invite.isExpired()) {
      throw new BusinessException("Invite has expired", ErrorCode.TOKEN_EXPIRED);
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

  @Transactional
  public InviteResponseDto resendInvite(UUID inviteId) {
    checkInvitePermissions();

    Invite invite = repository.findById(inviteId)
            .orElseThrow(() -> new NotFoundException(INVITE_NOT_FOUND_MSG));

    if (invite.getStatus() == InviteStatus.ACCEPTED) {
      throw new BusinessException("Cannot resend an already accepted invitation", ErrorCode.TOKEN_REVOKED);
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

  @Transactional
  public void cancelInvite(UUID inviteId) {
    checkInvitePermissions();
    Invite invite = repository.findById(inviteId)
            .orElseThrow(() -> new NotFoundException(INVITE_NOT_FOUND_MSG));
    invite.cancel();
    repository.save(invite);
  }
}
