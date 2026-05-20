package com.safo.icos.modules.auth.presentation;

import com.safo.icos.common.dto.UserDto;
import com.safo.icos.common.port.user.UserProvider;
import com.safo.icos.modules.auth.application.AuthService;
import com.safo.icos.modules.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.auth.application.dto.LoginRequestDto;
import com.safo.icos.modules.auth.application.dto.RefreshResponseDto;
import com.safo.icos.modules.auth.application.dto.RegisterRequestDto;
import com.safo.icos.modules.auth.presentation.dto.AuthResponseView;
import com.safo.icos.modules.auth.presentation.dto.RefreshResponseView;
import jakarta.servlet.http.HttpServletResponse;
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
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthGraphQLResolver {

    private final AuthService authService;
    private final UserProvider userProvider;

    private HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    private void setRefreshTokenCookie(String token) {
        HttpServletResponse response = getResponse();
        if (response != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(30))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
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

        String device = deviceInfo != null ? deviceInfo : "graphql-client";
        AuthResponseDto result = authService.register(request, device);
        setRefreshTokenCookie(result.getRefreshToken());
        return AuthResponseView.builder()
                .accessToken(result.getAccessToken())
                .tokenType(result.getTokenType())
                .expiresIn(result.getExpiresIn())
                .user(result.getUser())
                .build();
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

        String device = deviceInfo != null ? deviceInfo : "graphql-client";
        AuthResponseDto result = authService.login(request, device);
        setRefreshTokenCookie(result.getRefreshToken());
        return AuthResponseView.builder()
                .accessToken(result.getAccessToken())
                .tokenType(result.getTokenType())
                .expiresIn(result.getExpiresIn())
                .user(result.getUser())
                .build();
    }

    @MutationMapping
    public RefreshResponseView refresh(
            @Argument String refreshToken,
            @ContextValue(name = "deviceInfo", required = false) String deviceInfo
    ) {
        String device = deviceInfo != null ? deviceInfo : "graphql-client";
        RefreshResponseDto result = authService.refresh(refreshToken, device);
        setRefreshTokenCookie(result.getRefreshToken());
        return RefreshResponseView.builder()
                .accessToken(result.getAccessToken())
                .expiresIn(result.getExpiresIn())
                .build();
    }

    @MutationMapping
    public boolean logout() {
        HttpServletResponse response = getResponse();
        if (response != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
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
    public UserDto me(@ContextValue(name = "userId", required = true) String userId) {
        return userProvider.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}