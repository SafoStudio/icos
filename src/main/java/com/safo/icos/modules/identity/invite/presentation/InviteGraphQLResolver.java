package com.safo.icos.modules.identity.invite.presentation;

import com.safo.icos.modules.identity.auth.application.AuthService;
import com.safo.icos.modules.identity.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.identity.invite.application.InviteService;
import com.safo.icos.modules.identity.invite.application.dto.InviteAcceptDto;
import com.safo.icos.modules.identity.invite.application.dto.InviteRequestDto;
import com.safo.icos.modules.identity.invite.application.dto.InviteResponseDto;
import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.security.context.TenantContext;
import com.safo.icos.security.permission.RequirePermission;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequiredArgsConstructor
public class InviteGraphQLResolver {
  private final InviteService inviteService;
  private final AuthService authService;
  private final InviteRepository inviteRepository;

  @MutationMapping
  @RequirePermission("invite:manage")
  public InviteResponseDto createInvite(@Argument InviteRequestDto input) {
    UUID companyId = UUID.fromString(TenantContext.getTenantId());
    return inviteService.createInvite(companyId, input);
  }

  @QueryMapping
  public InviteResponseDto getInviteInfo(@Argument String token) {
    return inviteService.getInviteByToken(token);
  }

  @MutationMapping
  @Transactional
  public AuthResponseDto acceptInvite(@Argument String token, @Argument InviteAcceptDto input) {
    Invite invite = inviteRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invite not found"));

    invite.accept();
    inviteRepository.save(invite);

    return authService.registerFromInvite(invite, input, "graphql-client");
  }

  @MutationMapping
  @RequirePermission("invite:manage")
  public Boolean cancelInvite(@Argument UUID id) {
    inviteService.cancelInvite(id);
    return true;
  }

  @MutationMapping
  @RequirePermission("invite:manage")
  public InviteResponseDto resendInvite(@Argument UUID id) {
    return inviteService.resendInvite(id);
  }
}
