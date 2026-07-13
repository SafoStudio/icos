package com.safo.icos.security.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantContextTest {

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  void shouldSetAndGetTenantId() {
    TenantContext.setTenantId("tenant-123");

    assertThat(TenantContext.getTenantId()).isEqualTo("tenant-123");
  }

  @Test
  void shouldReturnNullWhenNotSet() {
    assertThat(TenantContext.getTenantId()).isNull();
  }

  @Test
  void shouldClearTenantId() {
    TenantContext.setTenantId("tenant-123");
    TenantContext.clear();

    assertThat(TenantContext.getTenantId()).isNull();
  }

  @Test
  void shouldOverwriteTenantId() {
    TenantContext.setTenantId("tenant-1");
    TenantContext.setTenantId("tenant-2");

    assertThat(TenantContext.getTenantId()).isEqualTo("tenant-2");
  }
}