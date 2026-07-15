package com.safo.icos.security.jwt;

import com.safo.icos.modules.identity.user.api.UserProvider;
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

/**
 * JWT authentication filter that runs once per request.
 * Extracts the Bearer token from the {@code Authorization} header,
 * validates it, and populates the Spring Security context
 * with an {@link IcosUserPrincipal}.
 * <p>
 * Also sets the {@link TenantContext} for multi-tenant data scoping.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserProvider userProvider;

  /**
   * Processes each incoming HTTP request:
   * <ol>
   *   <li>Extracts the Bearer token from the Authorization header.</li>
   *   <li>Validates the token's signature and expiration.</li>
   *   <li>Extracts userId and tenantId claims.</li>
   *   <li>Sets the tenant context.</li>
   *   <li>Loads the full principal from the database.</li>
   *   <li>Sets the Spring Security authentication context.</li>
   *   <li>Clears the tenant context in a {@code finally} block.</li>
   * </ol>
   * If the token is missing or invalid, the request continues unauthenticated.
   *
   * @param request     the HTTP request
   * @param response    the HTTP response
   * @param filterChain the filter chain
   */
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

            var principalData = userProvider.findPrincipal(userId, tenantId);

            if (principalData.isPresent()) {
              var data = principalData.get();
              List<SimpleGrantedAuthority> authorities =
                      data.getPermissions().stream()
                              .map(SimpleGrantedAuthority::new)
                              .toList();

              IcosUserPrincipal principal = IcosUserPrincipal.builder()
                      .userId(data.getUserId())
                      .email(data.getEmail())
                      .membershipId(data.getMembershipId())
                      .companyId(data.getCompanyId())
                      .roleName(data.getRoleName())
                      .departmentId(data.getDepartmentId())
                      .departmentName(data.getDepartmentName())
                      .authorities(authorities)
                      .build();

              UsernamePasswordAuthenticationToken authentication =
                      new UsernamePasswordAuthenticationToken(
                              principal, null, principal.getAuthorities());

              SecurityContextHolder.getContext()
                      .setAuthentication(authentication);
            }
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
