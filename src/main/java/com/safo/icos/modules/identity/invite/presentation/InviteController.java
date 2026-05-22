package com.safo.icos.modules.invite.presentation;

import com.safo.icos.modules.auth.application.AuthService;
import com.safo.icos.modules.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.invite.application.InviteService;
import com.safo.icos.modules.invite.application.dto.InviteAcceptDto;
import com.safo.icos.modules.invite.application.dto.InviteRequestDto;
import com.safo.icos.modules.invite.application.dto.InviteResponseDto;
import com.safo.icos.modules.invite.domain.model.Invite;
import com.safo.icos.modules.invite.domain.repository.InviteRepository;
import com.safo.icos.security.permission.RequirePermission;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InviteController {
  private final InviteService inviteService;
  private final AuthService authService;
  private final InviteRepository inviteRepository;

  @PostMapping("/companies/{companyId}/invites")
  @RequirePermission("invite:manage")
  public ResponseEntity<InviteResponseDto> createInvite(
          @PathVariable UUID companyId,
          @Valid @RequestBody InviteRequestDto dto) {
    return ResponseEntity.ok(inviteService.createInvite(companyId, dto));
  }

  @GetMapping("/public/invites/{token}")
  public ResponseEntity<InviteResponseDto> getInviteInfo(@PathVariable String token) {
    return ResponseEntity.ok(inviteService.getInviteByToken(token));
  }

  @PostMapping("/public/invites/{token}/accept")
  @Transactional
  public ResponseEntity<AuthResponseDto> acceptInvite(
          @PathVariable String token,
          @Valid @RequestBody InviteAcceptDto dto,
          @RequestHeader(value = "User-Agent", defaultValue = "unknown") String deviceInfo) {

    Invite invite = inviteRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invite not found"));

    invite.accept();
    inviteRepository.save(invite);

    AuthResponseDto response = authService.registerFromInvite(invite, dto, deviceInfo);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/invites/{id}")
  @RequirePermission("invite:manage")
  public ResponseEntity<Void> cancelInvite(@PathVariable UUID id) {
    inviteService.cancelInvite(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/invites/{id}/resend")
  @RequirePermission("invite:manage")
  public ResponseEntity<InviteResponseDto> resendInvite(@PathVariable UUID id) {
    return ResponseEntity.ok(inviteService.resendInvite(id));
  }
}
