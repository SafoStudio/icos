package com.safo.icos.modules.identity.auth.presentation;

import com.safo.icos.modules.identity.auth.application.AuthService;
import com.safo.icos.modules.identity.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.identity.auth.application.dto.LoginRequestDto;
import com.safo.icos.modules.identity.auth.application.dto.RefreshResponseDto;
import com.safo.icos.modules.identity.auth.application.dto.RegisterRequestDto;
import com.safo.icos.modules.identity.auth.presentation.dto.AuthResponseView;
import com.safo.icos.modules.identity.auth.presentation.dto.RefreshResponseView;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthGraphQLResolver {

  private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
  private static final String DEFAULT_DEVICE = "graphql-client";
  private static final Duration COOKIE_MAX_AGE = Duration.ofDays(30);

  private final AuthService authService;
  private final UserProvider userProvider;

  private HttpServletRequest getRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }

  private HttpServletResponse getResponse() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getResponse() : null;
  }

  private String getRefreshTokenFromCookie() {
    HttpServletRequest request = getRequest();
    if (request != null && request.getCookies() != null) {
      for (var cookie : request.getCookies()) {
        if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  private void setRefreshTokenCookie(String token) {
    HttpServletResponse response = getResponse();
    if (response != null) {
      ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
              .httpOnly(true)
              .secure(true)
              .sameSite("Strict")
              .path("/")
              .maxAge(COOKIE_MAX_AGE)
              .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
  }

  private String resolveDevice(String deviceInfo) {
    return deviceInfo != null ? deviceInfo : DEFAULT_DEVICE;
  }

  private AuthResponseView mapToAuthResponseView(AuthResponseDto result) {
    setRefreshTokenCookie(result.getRefreshToken());
    return AuthResponseView.builder()
            .accessToken(result.getAccessToken())
            .tokenType(result.getTokenType())
            .expiresIn(result.getExpiresIn())
            .user(result.getUser())
            .build();
  }

  @MutationMapping
  public AuthResponseView register(
          @Argument String email,
          @Argument String password,
          @Argument String firstName,
          @Argument String lastName,
          @Argument String companyName,
          @ContextValue(name = "deviceInfo", required = false) String deviceInfo
  ) {
    var request = RegisterRequestDto.builder()
            .email(email)
            .password(password)
            .firstName(firstName)
            .lastName(lastName)
            .companyName(companyName)
            .build();

    return mapToAuthResponseView(authService.register(request, resolveDevice(deviceInfo)));
  }

  @MutationMapping
  public AuthResponseView login(
          @Argument String email,
          @Argument String password,
          @ContextValue(name = "deviceInfo", required = false) String deviceInfo
  ) {
    var request = LoginRequestDto.builder()
            .email(email)
            .password(password)
            .build();

    return mapToAuthResponseView(authService.login(request, resolveDevice(deviceInfo)));
  }

  @MutationMapping
  public RefreshResponseView refresh(
          @Argument(name = "refreshToken") String refreshTokenArg,
          @ContextValue(name = "deviceInfo", required = false) String deviceInfo
  ) {
    String refreshToken = refreshTokenArg;
    if (refreshToken == null || refreshToken.isBlank()) {
      refreshToken = getRefreshTokenFromCookie();
    }
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new IllegalArgumentException("Refresh token is missing");
    }
    RefreshResponseDto result = authService.refresh(refreshToken, resolveDevice(deviceInfo));
    setRefreshTokenCookie(result.getRefreshToken());
    return RefreshResponseView.builder()
            .accessToken(result.getAccessToken())
            .expiresIn(result.getExpiresIn())
            .build();
  }

  @MutationMapping
  public boolean logout(@Argument(name = "refreshToken") String refreshTokenArg) {
    String refreshToken = refreshTokenArg;
    if (refreshToken == null || refreshToken.isBlank()) {
      refreshToken = getRefreshTokenFromCookie();
    }
    if (refreshToken != null && !refreshToken.isBlank()) {
      authService.logout(refreshToken);
    }
    HttpServletResponse response = getResponse();
    if (response != null) {
      ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
              .httpOnly(true)
              .secure(true)
              .sameSite("Strict")
              .path("/")
              .maxAge(0)
              .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    return true;
  }

  @QueryMapping
  public UserDto me(@ContextValue(name = "userId") String userId) {
    return userProvider.findById(UUID.fromString(userId))
            .orElseThrow(() -> new RuntimeException("User not found"));
  }
}
