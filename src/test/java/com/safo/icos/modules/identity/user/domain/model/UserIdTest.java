package com.safo.icos.modules.identity.user.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserIdTest {

  @Test
  void shouldGenerateUniqueIds() {
    UserId id1 = UserId.generate();
    UserId id2 = UserId.generate();

    assertThat(id1).isNotEqualTo(id2);
    assertThat(id1.getValue()).isNotNull();
  }

  @Test
  void shouldCreateFromUUID() {
    UUID uuid = UUID.randomUUID();

    UserId id = UserId.fromUUID(uuid);

    assertThat(id.getValue()).isEqualTo(uuid);
  }

  @Test
  void shouldCreateFromString() {
    UUID uuid = UUID.randomUUID();

    UserId id = UserId.fromString(uuid.toString());

    assertThat(id.getValue()).isEqualTo(uuid);
  }

  @Test
  void shouldThrowWhenUUIDIsNull() {
    assertThatThrownBy(() -> UserId.fromUUID(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("null");
  }

  @Test
  void shouldThrowForInvalidUuidString() {
    assertThatThrownBy(() -> UserId.fromString("not-a-uuid"))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldBeEqualForSameValue() {
    UUID uuid = UUID.randomUUID();
    UserId id1 = UserId.fromUUID(uuid);
    UserId id2 = UserId.fromUUID(uuid);

    assertThat(id1).isEqualTo(id2);
  }

  @Test
  void shouldConvertToString() {
    UUID uuid = UUID.randomUUID();
    UserId id = UserId.fromUUID(uuid);

    assertThat(id.toString()).isEqualTo(uuid.toString());
  }
}