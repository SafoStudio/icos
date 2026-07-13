package com.safo.icos.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldAllowGraphqlPostWithoutAuthentication() throws Exception {
    mockMvc.perform(post("/graphql")
                    .contentType("application/json")
                    .content("{\"query\":\"{ __typename }\"}"))
            .andExpect(status().isOk());
  }

  @Test
  void shouldRejectProtectedEndpointWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/api/protected/resource"))
            .andExpect(status().isForbidden());
  }
}