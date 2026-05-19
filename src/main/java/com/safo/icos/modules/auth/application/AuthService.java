// modules/auth/application/AuthService.java
package com.safo.icos.modules.auth.application;

import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.common.dto.AuthTokenDto;
import com.safo.icos.common.dto.MembershipDto;
import com.safo.icos.common.dto.UserDto;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.port.auth.AuthTokenProvider;
import com.safo.icos.common.port.user.UserCreateData;
import com.safo.icos.common.port.user.UserProvider;
import com.safo.icos.modules.auth.application.dto.AuthResponseDto;
import com.safo.icos.modules.auth.application.dto.LoginRequestDto;
import com.safo.icos.modules.auth.application.dto.RefreshResponseDto;
import com.safo.icos.modules.auth.application.dto.RegisterRequestDto;
import com.safo.icos.modules.auth.domain.exception.InvalidCredentialsException;
import com.safo.icos.modules.auth.domain.exception.UserAlreadyExistsException;
import com.safo.icos.modules.auth.domain.model.RefreshToken;
import com.safo.icos.modules.auth.domain.repository.RefreshTokenRepository;
import com.safo.icos.modules.auth.domain.service.AuthDomainService;
import com.safo.icos.modules.auth.infrastructure.repository.AuthCredentialsJpaEntity;
import com.safo.icos.modules.auth.infrastructure.repository.AuthCredentialsJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserProvider userProvider;
    private final AuthTokenProvider authTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthDomainService authDomainService;
    private final PasswordEncoder passwordEncoder;
    private final AuthCredentialsJpaRepository credentialsRepository;

    @Value("${app.auth.refresh-token-ttl:30d}")
    private Duration refreshTokenTtl;

    public AuthResponseDto register(RegisterRequestDto request, String deviceInfo) {
        log.debug("Registering new user with email: {}", request.getEmail());

        if (userProvider.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserCreateData createData = UserCreateData.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdBy(null)
                .build();

        UserDto user = userProvider.createUser(createData);

        // Save password
        AuthCredentialsJpaEntity credentials = AuthCredentialsJpaEntity.builder()
                .userId(user.getId())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        credentialsRepository.save(credentials);

        UUID companyId = UUID.fromString(request.getCompanyId());
        String role = request.getRole() != null ? request.getRole() : "OWNER";
        MembershipDto membership = userProvider.createMembership(user.getId(), companyId, role, null);

        RefreshToken refreshToken = authDomainService.createRefreshToken(
                membership.getId(), deviceInfo, refreshTokenTtl);
        refreshTokenRepository.save(refreshToken);

        AuthTokenDto tokens = authTokenProvider.generateTokens(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                membership.getCompanyId()
        );

        return AuthResponseDto.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .tokenType(tokens.getTokenType())
                .expiresIn(tokens.getExpiresIn())
                .user(user)
                .build();
    }

    public AuthResponseDto login(LoginRequestDto request, String deviceInfo) {
        log.debug("Login attempt for email: {}", request.getEmail());

        UserDto user = userProvider.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        AuthCredentialsJpaEntity credentials = credentialsRepository.findById(user.getId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), credentials.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        UUID companyId = UUID.fromString(request.getCompanyId());
        MembershipDto membership = userProvider.findActiveMembership(user.getId(), companyId)
                .orElseThrow(() -> new BusinessException(
                        "User is not active in this company",
                        ErrorCode.USER_NOT_ACTIVE));

        RefreshToken refreshToken = authDomainService.createRefreshToken(
                membership.getId(), deviceInfo, refreshTokenTtl);
        refreshTokenRepository.save(refreshToken);

        AuthTokenDto tokens = authTokenProvider.generateTokens(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                membership.getCompanyId()
        );

        return AuthResponseDto.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .tokenType(tokens.getTokenType())
                .expiresIn(tokens.getExpiresIn())
                .user(user)
                .build();
    }

    public RefreshResponseDto refresh(String refreshTokenValue, String deviceInfo) {
        log.debug("Refreshing token");

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(refreshTokenValue)
                .orElseThrow(() -> new BusinessException("Invalid refresh token", ErrorCode.TOKEN_NOT_FOUND));

        authDomainService.validateRefreshToken(refreshToken);

        // Rotation: revoke old token
        RefreshToken revoked = refreshToken.revoke();
        refreshTokenRepository.save(revoked);

        // Get membership and user details
        MembershipDto membership = userProvider.findMembershipById(refreshToken.getMembershipId())
                .orElseThrow(() -> new BusinessException("Membership not found", ErrorCode.USER_NOT_FOUND));

        UserDto user = userProvider.findById(membership.getUserId())
                .orElseThrow(() -> new BusinessException("User not found", ErrorCode.USER_NOT_FOUND));

        // Create new refresh token
        RefreshToken newToken = authDomainService.createRefreshToken(
                membership.getId(), deviceInfo, refreshTokenTtl);
        refreshTokenRepository.save(newToken);

        // Generate new JWT
        AuthTokenDto tokens = authTokenProvider.generateTokens(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                membership.getCompanyId()
        );

        return RefreshResponseDto.builder()
                .accessToken(tokens.getAccessToken())
                .refreshToken(tokens.getRefreshToken())
                .expiresIn(tokens.getExpiresIn())
                .build();
    }

    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByTokenHash(refreshTokenValue)
                .ifPresent(token -> {
                    RefreshToken revoked = token.revoke();
                    refreshTokenRepository.save(revoked);
                });
    }
}