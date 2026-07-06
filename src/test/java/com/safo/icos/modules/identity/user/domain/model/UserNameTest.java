package com.safo.icos.modules.identity.user.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserNameTest {

  @Test
  void shouldCreateValidUserName() {
    UserName name = UserName.of("John", "Doe");

    assertThat(name.getFirstName()).isEqualTo("John");
    assertThat(name.getLastName()).isEqualTo("Doe");
    assertThat(name.getFullName()).isEqualTo("John Doe");
  }

  @Test
  void shouldTrimWhitespace() {
    UserName name = UserName.of("  John  ", "  Doe  ");

    assertThat(name.getFirstName()).isEqualTo("John");
    assertThat(name.getLastName()).isEqualTo("Doe");
  }

  @Test
  void shouldThrowWhenFirstNameBlank() {
    assertThatThrownBy(() -> UserName.of("", "Doe"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name");
  }

  @Test
  void shouldThrowWhenFirstNameNull() {
    assertThatThrownBy(() -> UserName.of(null, "Doe"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("First name");
  }

  @Test
  void shouldThrowWhenLastNameBlank() {
    assertThatThrownBy(() -> UserName.of("John", ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Last name");
  }

  @Test
  void shouldThrowWhenFirstNameTooLong() {
    String longName = "A".repeat(101);

    assertThatThrownBy(() -> UserName.of(longName, "Doe"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("100");
  }

  @Test
  void shouldThrowWhenLastNameTooLong() {
    String longName = "A".repeat(101);

    assertThatThrownBy(() -> UserName.of("John", longName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("100");
  }
}