package com.safo.icos.modules.identity.user.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

  @Test
  void shouldCreateValidEmail() {
    Email email = Email.of("Test@Example.Com");

    assertThat(email.value()).isEqualTo("test@example.com");
  }

  @Test
  void shouldCreateEmailViaConstructor() {
    Email email = new Email("user@domain.com");

    assertThat(email.value()).isEqualTo("user@domain.com");
  }

  @Test
  void shouldBeEqualForSameValue() {
    Email email1 = Email.of("test@example.com");
    Email email2 = Email.of("test@example.com");

    assertThat(email1).isEqualTo(email2);
  }

  @Test
  void shouldNotBeEqualForDifferentValue() {
    Email email1 = Email.of("test@example.com");
    Email email2 = Email.of("other@example.com");

    assertThat(email1).isNotEqualTo(email2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"invalid", "missing-at", "@no-local", "no-domain@", "space in@email.com"})
  void shouldThrowForInvalidFormat(String invalidEmail) {
    assertThatThrownBy(() -> Email.of(invalidEmail))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
  }

  @ParameterizedTest
  @NullAndEmptySource
  void shouldThrowForNullOrEmpty(String invalidEmail) {
    assertThatThrownBy(() -> Email.of(invalidEmail))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("empty");
  }
}
