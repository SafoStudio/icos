package com.safo.icos.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for cryptographic hashing operations.
 * Provides methods to generate secure hash values using standard algorithms.
 *
 * <p>This class is designed as a thread-safe utility with no instance state.
 * All methods are static and should be accessed directly via the class name.
 *
 * <p>Currently supports SHA-256 hashing algorithm with Base64 encoding.
 * The resulting hash is suitable for password storage, data integrity checks,
 * and other security-sensitive operations.
 *
 * @author Icos Team
 * @version 1.0
 * @since 1.0.0
 */
public class HashUtils {

  /** The cryptographic algorithm used for hashing operations. */
  private static final String ALGORITHM = "SHA-256";

  /**
   * Private constructor to prevent instantiation.
   * This utility class should only be used via its static methods.
   */
  private HashUtils() {
    /* This utility class should not be instantiated */
  }

  /**
   * Generates a SHA-256 hash of the input string and encodes it in Base64 format.
   *
   * <p>This method uses UTF-8 encoding for string conversion and returns
   * a Base64-encoded hash string. The hash is deterministic - the same input
   * will always produce the same output.
   *
   * <p><b>Security Considerations:</b>
   * <ul>
   *   <li>SHA-256 is cryptographically secure for most applications</li>
   *   <li>For password storage, consider using bcrypt or PBKDF2 with salt</li>
   *   <li>Base64 encoding is URL-safe and suitable for storage in databases</li>
   * </ul>
   *
   * <p><b>Usage Example:</b>
   * <pre>
   * String passwordHash = HashUtils.hash("mySecurePassword123");
   * // Result: Base64 encoded SHA-256 hash
   * </pre>
   *
   * @param value the input string to be hashed (must not be null)
   * @return a Base64-encoded SHA-256 hash of the input string
   * @throws IllegalStateException if the SHA-256 algorithm is not available
   *         in the current Java environment (should never occur in standard JVMs)
   * @throws NullPointerException if the input value is null
   */
  public static String hash(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
      byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(
              "SHA-256 algorithm is not available in the current Java environment. " +
                      "Please verify your Java installation and security providers.", e
      );
    }
  }
}
