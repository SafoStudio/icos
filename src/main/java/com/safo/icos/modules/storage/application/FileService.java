package com.safo.icos.modules.storage.application;

import com.safo.icos.modules.identity.user.application.UserService;
import com.safo.icos.modules.identity.user.application.dto.UserResponseDto;
import com.safo.icos.modules.storage.api.StorageProvider;

import java.io.InputStream;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * Application service for file storage operations.
 * Delegates actual file I/O to a {@link StorageProvider} implementation
 * and updates user metadata via {@link UserService}.
 */
@Service
@RequiredArgsConstructor
public class FileService {

  private final StorageProvider storageProvider;
  private final UserService userService;

  /**
   * Uploads an avatar image for the given user.
   * Generates a storage path based on company ID and user email,
   * uploads the file, and updates the user's avatar path in the database.
   *
   * @param userId           the user's UUID
   * @param inputStream      the file content stream
   * @param originalFilename original file name (used to build the path)
   * @param contentType      MIME type of the file
   * @return the generated storage path
   */
  @SneakyThrows
  public String uploadAvatar(UUID userId, InputStream inputStream,
                             String originalFilename, String contentType) {
    UserResponseDto user = userService.getUserById(userId);
    String path = storageProvider.getAvatarPath(
            user.getCompanyId(), user.getEmail(), originalFilename);

    storageProvider.uploadFile(path, inputStream, contentType);
    userService.updateAvatarPath(userId, path);

    return path;
  }

  /**
   * Deletes the user's current avatar from storage and clears the avatar path.
   *
   * @param userId the user's UUID
   */
  public void deleteAvatar(UUID userId) {
    UserResponseDto user = userService.getUserById(userId);
    if (user.getProfile() != null && user.getProfile().getAvatarPath() != null) {
      storageProvider.deleteFile(user.getProfile().getAvatarPath());
      userService.updateAvatarPath(userId, null);
    }
  }

  /**
   * Retrieves the user's avatar image content as bytes.
   *
   * @param userId the user's UUID
   * @return byte array of avatar image, or null if not found
   */
  public byte[] getAvatarBytes(UUID userId) {
    UserResponseDto user = userService.getUserById(userId);
    if (user.getProfile() == null || user.getProfile().getAvatarPath() == null) {
      return null;
    }
    String path = user.getProfile().getAvatarPath();
    try (InputStream is = storageProvider.getFile(path)) {
      if (is == null) {
        return null;
      }
      return is.readAllBytes();
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Uploads a document for the given user.
   * Generates a storage path, uploads the file, and saves document metadata.
   *
   * @param userId      the user's UUID
   * @param docType     document type (e.g., "passport", "diploma")
   * @param filename    original file name
   * @param inputStream the file content stream
   * @param contentType MIME type of the file
   * @return the generated storage path
   */
  @SneakyThrows
  public String uploadDocument(UUID userId, String docType, String filename,
                               InputStream inputStream, String contentType, long size) {
    UserResponseDto user = userService.getUserById(userId);
    String path = storageProvider.getDocumentPath(
            user.getCompanyId(), user.getEmail(), docType, filename);

    storageProvider.uploadFile(path, inputStream, contentType);
    userService.saveDocumentMetadata(userId, docType, filename, path, contentType, size);
    return path;
  }

  @SneakyThrows
  public String uploadDocument(UUID userId, String docType, String filename,
                               InputStream inputStream, String contentType) {
    return uploadDocument(userId, docType, filename, inputStream, contentType, inputStream.available());
  }

  /**
   * Deletes a document from storage and removes its metadata.
   *
   * @param userId   the user's UUID
   * @param docType  document type
   * @param filename original file name
   */
  public void deleteDocument(UUID userId, String docType, String filename) {
    UserResponseDto user = userService.getUserById(userId);
    String path = storageProvider.getDocumentPath(
            user.getCompanyId(), user.getEmail(), docType, filename);

    storageProvider.deleteFile(path);
    userService.deleteDocumentMetadata(userId, docType, filename);
  }

  /**
   * Deletes a document by its ID from storage and removes metadata.
   */
  public void deleteDocumentById(UUID userId, UUID documentId) {
    var doc = userService.getDocumentEntity(userId, documentId);
    if (doc != null) {
      storageProvider.deleteFile(doc.getFilePath());
      userService.deleteDocumentById(userId, documentId);
    }
  }

  /**
   * Retrieves the raw content bytes of a user's document.
   */
  public byte[] getDocumentBytes(UUID userId, String docType, String filename) {
    UserResponseDto user = userService.getUserById(userId);
    String path = storageProvider.getDocumentPath(
            user.getCompanyId(), user.getEmail(), docType, filename);

    try (InputStream is = storageProvider.getFile(path)) {
      if (is != null) {
        return is.readAllBytes();
      }
    } catch (Exception ignored) {
    }

    if (user.getDocuments() != null) {
      for (var doc : user.getDocuments()) {
        if (doc.getFilename() != null && doc.getFilename().equalsIgnoreCase(filename)) {
          try (InputStream is = storageProvider.getFile(doc.getFilePath())) {
            if (is != null) {
              return is.readAllBytes();
            }
          } catch (Exception ignored) {
          }
        }
      }
    }
    return null;
  }

  /**
   * Retrieves the raw content bytes of a user's document by document ID.
   */
  public byte[] getDocumentBytesById(UUID userId, UUID documentId) {
    var doc = userService.getDocumentEntity(userId, documentId);
    if (doc == null) return null;
    try (InputStream is = storageProvider.getFile(doc.getFilePath())) {
      if (is != null) {
        return is.readAllBytes();
      }
    } catch (Exception ignored) {
    }
    return null;
  }

  /**
   * Determines content type for the user document.
   */
  public String getDocumentContentType(UUID userId, String docType, String filename) {
    UserResponseDto user = userService.getUserById(userId);
    if (user.getDocuments() != null) {
      for (var doc : user.getDocuments()) {
        if (doc.getFilename() != null && doc.getFilename().equalsIgnoreCase(filename)
                && doc.getContentType() != null) {
          return doc.getContentType();
        }
      }
    }
    return resolveContentType(filename);
  }

  public String getDocumentContentTypeById(UUID userId, UUID documentId) {
    var doc = userService.getDocumentEntity(userId, documentId);
    if (doc != null && doc.getContentType() != null && !doc.getContentType().isBlank()) {
      return doc.getContentType();
    }
    return doc != null ? resolveContentType(doc.getFilename()) : "application/octet-stream";
  }

  public String getDocumentFilenameById(UUID userId, UUID documentId) {
    var doc = userService.getDocumentEntity(userId, documentId);
    return doc != null ? doc.getFilename() : "document";
  }

  private String resolveContentType(String filename) {
    if (filename == null) return "application/octet-stream";
    String lower = filename.toLowerCase();
    if (lower.endsWith(".pdf")) return "application/pdf";
    if (lower.endsWith(".png")) return "image/png";
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
    if (lower.endsWith(".webp")) return "image/webp";
    if (lower.endsWith(".svg")) return "image/svg+xml";
    if (lower.endsWith(".txt")) return "text/plain";
    if (lower.endsWith(".doc")) return "application/msword";
    if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    return "application/octet-stream";
  }

  /**
   * Lists all metadata documents for a given user.
   */
  public java.util.List<com.safo.icos.modules.identity.user.application.dto.DocumentResponseDto> getUserDocuments(UUID userId) {
    UserResponseDto user = userService.getUserById(userId);
    return user.getDocuments() != null ? user.getDocuments() : java.util.List.of();
  }
}
