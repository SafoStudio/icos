package com.safo.icos.modules.identity.auth.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenIdTest {

  @Test
  void shouldGenerateUniqueIds() {
    RefreshTokenId id1 = RefreshTokenId.generate();
    RefreshTokenId id2 = RefreshTokenId.generate();

    assertThat(id1).isNotEqualTo(id2);
    assertThat(id1.value()).isNotNull();
  }

  @Test
  void shouldCreateFromString() {
    UUID uuid = UUID.randomUUID();

    RefreshTokenId id = RefreshTokenId.fromString(uuid.toString());

    assertThat(id.value()).isEqualTo(uuid);
  }

  @Test
  void shouldRejectNullValue() {
    assertThatThrownBy(() -> new RefreshTokenId(null))
            .isInstanceOf(NullPointerException.class);
  }

  @Test
  void shouldRejectInvalidUuidString() {
    assertThatThrownBy(() -> RefreshTokenId.fromString("not-a-uuid"))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldConvertToString() {
    UUID uuid = UUID.randomUUID();
    RefreshTokenId id = new RefreshTokenId(uuid);

    assertThat(id).hasToString(uuid.toString());
  }
}
