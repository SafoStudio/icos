package com.safo.icos.security.jwt;

import com.safo.icos.modules.identity.auth.api.AuthTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies JWT authentication end-to-end:
 * valid tokens for non-existent users return 403,
 * missing/invalid tokens return 403 (Spring Security default).
 */
@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private AuthTokenProvider authTokenProvider;

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID TENANT_ID = UUID.randomUUID();

  @Test
  void shouldRejectRequestWithoutToken() throws Exception {
    mockMvc.perform(get("/api/protected/resource"))
            .andExpect(status().isForbidden());
  }

  @Test
  void shouldRejectRequestWithInvalidToken() throws Exception {
    mockMvc.perform(get("/api/protected/resource")
                    .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isForbidden());
  }

  @Test
  void shouldRejectRequestWithTamperedToken() throws Exception {
    String validToken = authTokenProvider.generateAccessToken(USER_ID, TENANT_ID)
            .getAccessToken();

    mockMvc.perform(get("/api/protected/resource")
                    .header("Authorization", "Bearer " + validToken + "x"))
            .andExpect(status().isForbidden());
  }

  @Test
  void shouldRejectRequestWithValidTokenForNonExistentUser() throws Exception {
    String validToken = authTokenProvider.generateAccessToken(USER_ID, TENANT_ID)
            .getAccessToken();

    mockMvc.perform(get("/api/protected/resource")
                    .header("Authorization", "Bearer " + validToken))
            .andExpect(status().isForbidden());
  }
}