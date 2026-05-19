package com.safo.icos.common.port.auth;

import com.safo.icos.common.dto.AuthTokenDto;
import java.util.UUID;

public interface AuthTokenProvider {
    AuthTokenDto generateTokens(UUID userId, String email, String firstName, String lastName, UUID tenantId);
    boolean validateToken(String token);
    String extractUserId(String token);
    String extractTenantId(String token);
}