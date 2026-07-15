package com.safo.icos.modules.storage.application;

import com.safo.icos.modules.identity.user.application.service.UserService;
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
                               InputStream inputStream, String contentType) {
    UserResponseDto user = userService.getUserById(userId);
    String path = storageProvider.getDocumentPath(
            user.getCompanyId(), user.getEmail(), docType, filename);

    long size = inputStream.available();
    storageProvider.uploadFile(path, inputStream, contentType);

    userService.saveDocumentMetadata(userId, docType, filename, path, contentType, size);
    return path;
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
}
