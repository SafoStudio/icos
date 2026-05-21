package com.safo.icos.security.jwt;

import com.safo.icos.common.port.user.UserProvider;
import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.context.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserProvider userProvider;

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain
  ) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      try {
        if (jwtService.validateToken(token)) {
          String userIdStr = jwtService.extractUserId(token);
          String tenantIdStr = jwtService.extractTenantId(token);

          if (userIdStr != null && tenantIdStr != null) {
            UUID userId = UUID.fromString(userIdStr);
            UUID tenantId = UUID.fromString(tenantIdStr);

            TenantContext.setTenantId(tenantIdStr);

            userProvider.findPrincipal(userId, tenantId).ifPresent(principalData -> {
              List<SimpleGrantedAuthority> authorities = principalData.getPermissions().stream()
                      .map(SimpleGrantedAuthority::new)
                      .toList();

              IcosUserPrincipal principal = IcosUserPrincipal.builder()
                      .userId(principalData.getUserId())
                      .email(principalData.getEmail())
                      .membershipId(principalData.getMembershipId())
                      .companyId(principalData.getCompanyId())
                      .roleName(principalData.getRoleName())
                      .departmentId(principalData.getDepartmentId())
                      .departmentName(principalData.getDepartmentName())
                      .authorities(authorities)
                      .build();

              UsernamePasswordAuthenticationToken authentication =
                      new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

              SecurityContextHolder.getContext().setAuthentication(authentication);
            });
          }
        }
      } catch (Exception e) {
        log.warn("Authentication failed: {}", e.getMessage());
      }
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }
}