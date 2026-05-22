package com.safo.icos.modules.auth.api;

import com.safo.icos.modules.auth.api.dto.AuthTokenDto;
import java.util.UUID;

public interface AuthTokenProvider {
  AuthTokenDto generateTokens(UUID userId, UUID tenantId);

  boolean validateToken(String token);

  String extractUserId(String token);

  String extractTenantId(String token);
}
