package com.safo.icos.modules.identity.auth.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenValueTest {

  @Test
  void shouldGenerateUniqueRawTokens() {
    String token1 = TokenValue.generateRaw();
    String token2 = TokenValue.generateRaw();

    assertThat(token1)
            .isNotBlank()
            .isNotEqualTo(token2);
  }

  @Test
  void shouldGenerateBase64UrlWithoutPadding() {
    String raw = TokenValue.generateRaw();

    assertThat(raw)
            .doesNotContain("+")
            .doesNotContain("/")
            .doesNotContain("=");
  }

  @Test
  void shouldCreateHashFromRawToken() {
    String raw = TokenValue.generateRaw();

    TokenValue hash = TokenValue.fromRaw(raw);

    assertThat(hash.hash()).isNotBlank();
    assertThat(hash.hash()).isNotEqualTo(raw);
  }

  @Test
  void shouldBeDeterministic() {
    String raw = "test-token-value";

    TokenValue hash1 = TokenValue.fromRaw(raw);
    TokenValue hash2 = TokenValue.fromRaw(raw);

    assertThat(hash1).isEqualTo(hash2);
    assertThat(hash1.hash()).isEqualTo(hash2.hash());
  }

  @Test
  void shouldMatchRawToken() {
    String raw = TokenValue.generateRaw();
    TokenValue hash = TokenValue.fromRaw(raw);

    assertThat(hash.matchesRaw(raw)).isTrue();
  }

  @Test
  void shouldNotMatchDifferentRawToken() {
    String raw = TokenValue.generateRaw();
    String other = TokenValue.generateRaw();
    TokenValue hash = TokenValue.fromRaw(raw);

    assertThat(hash.matchesRaw(other)).isFalse();
  }

  @Test
  void shouldNotMatchAfterRawTokenModified() {
    String raw = TokenValue.generateRaw();
    TokenValue hash = TokenValue.fromRaw(raw);

    assertThat(hash.matchesRaw(raw + "tampered")).isFalse();
  }

  @Test
  void shouldRejectNullHash() {
    assertThatThrownBy(() -> new TokenValue(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Token hash cannot be null");
  }

  @ParameterizedTest
  @EmptySource
  @ValueSource(strings = {" ", "  ", "\t", "\n"})
  void shouldRejectEmptyOrBlankHash(String invalidHash) {
    assertThatThrownBy(() -> new TokenValue(invalidHash))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Token hash cannot be blank");
  }
}