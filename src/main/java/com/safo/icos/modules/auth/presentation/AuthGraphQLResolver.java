package com.safo.icos.modules.auth.presentation;

import com.safo.icos.common.dto.UserDto;
import com.safo.icos.common.port.user.UserProvider;
import com.safo.icos.modules.auth.application.AuthService;
import com.safo.icos.modules.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.auth.application.dto.LoginRequestDto;
import com.safo.icos.modules.auth.application.dto.RefreshResponseDto;
import com.safo.icos.modules.auth.application.dto.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthGraphQLResolver {

    private final AuthService authService;
    private final UserProvider userProvider;

    @MutationMapping
    public AuthResponseDto register(
            @Argument String email,
            @Argument String password,
            @Argument String firstName,
            @Argument String lastName,
            @Argument String companyId,
            @Argument String role,
            @ContextValue(name = "deviceInfo", required = false) String deviceInfo
    ) {
        var request = RegisterRequestDto.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .companyId(companyId)
                .role(role)
                .build();

        String device = deviceInfo != null ? deviceInfo : "graphql-client";
        return authService.register(request, device);
    }

    @MutationMapping
    public AuthResponseDto login(
            @Argument String email,
            @Argument String password,
            @Argument String companyId,
            @ContextValue(name = "deviceInfo", required = false) String deviceInfo
    ) {
        var request = LoginRequestDto.builder()
                .email(email)
                .password(password)
                .companyId(companyId)
                .build();

        String device = deviceInfo != null ? deviceInfo : "graphql-client";
        return authService.login(request, device);
    }

    @MutationMapping
    public RefreshResponseDto refresh(
            @Argument String refreshToken,
            @ContextValue(name = "deviceInfo", required = false) String deviceInfo
    ) {
        String device = deviceInfo != null ? deviceInfo : "graphql-client";
        return authService.refresh(refreshToken, device);
    }

    @MutationMapping
    public boolean logout(@Argument String refreshToken) {
        authService.logout(refreshToken);
        return true;
    }

    @QueryMapping
    public UserDto me(@ContextValue(name = "userId", required = true) String userId) {
        return userProvider.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}