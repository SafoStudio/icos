package com.safo.icos.modules.identity.auth.domain.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

/**
 * A value object representing the refresh token hash.
 * Used for secure storage of tokens in the database and for retrieval.
 */
public record TokenValue(String hash) {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final String HASH_ALGORITHM = "SHA-256";

  public TokenValue {
    Objects.requireNonNull(hash, "Token hash cannot be null");
    if (hash.isBlank()) {
      throw new IllegalArgumentException("Token hash cannot be blank");
    }
  }

  /**
   * Generates a cryptographically strong raw token.
   * @return the raw token string (44 characters Base64url without padding)
   */
  public static String generateRaw() {
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  /**
   * Creates a {@link TokenValue} from a raw token by calculating its hash.
   * @param rawToken - raw token received from the client
   * @return value object with the hash
   */
  public static TokenValue fromRaw(String rawToken) {
    return new TokenValue(sha256(rawToken));
  }

  /**
   * Checks whether this hash matches the passed raw token.
   * @param rawToken raw token
   * @return true if the hash matches
   */
  public boolean matchesRaw(String rawToken) {
    return hash.equals(sha256(rawToken));
  }

  private static String sha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
      byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm not available", e);
    }
  }

  @Override
  public @NonNull String toString() {
    return hash;
  }
}