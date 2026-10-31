package com.safo.icos.seeder;

import com.safo.icos.modules.storage.api.StorageProvider;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "seed"})
@Slf4j
public class AvatarSeeder implements TableSeeder {

  private static final String AVATARS_SOURCE_PATH = "seed/avatars/";
  private static final String AVATARS_DEST_PATH = "avatars/";

  private final StorageProvider storageProvider;

  public AvatarSeeder(StorageProvider storageProvider) {
    this.storageProvider = storageProvider;
  }

  @Override
  public void seed() {
    try {
      Path sourceDir = getSourceDirectory();
      if (sourceDir == null) {
        return;
      }

      List<Path> avatarFiles = getAvatarFiles(sourceDir);
      if (avatarFiles.isEmpty()) {
        log.warn("No avatar files found in: {}", AVATARS_SOURCE_PATH);
        return;
      }

      SeedResult result = processAvatarFiles(avatarFiles);
      log.info("Avatar sync to MinIO completed. Uploaded: {}, Skipped: {}",
              result.uploaded, result.skipped);

    } catch (IOException e) {
      log.error("Failed to sync avatar files to MinIO", e);
    }
  }

  private Path getSourceDirectory() throws IOException {
    ClassPathResource resource = new ClassPathResource(AVATARS_SOURCE_PATH);
    if (!resource.exists()) {
      log.warn("Avatar source directory not found: {}", AVATARS_SOURCE_PATH);
      return null;
    }
    return resource.getFile().toPath();
  }

  private List<Path> getAvatarFiles(Path sourceDir) throws IOException {
    try (Stream<Path> stream = Files.list(sourceDir)) {
      return stream
              .filter(Files::isRegularFile)
              .filter(path -> path.toString().endsWith(".webp"))
              .sorted()
              .toList();
    }
  }

  private SeedResult processAvatarFiles(List<Path> avatarFiles) {
    int uploaded = 0;
    int skipped = 0;

    for (Path sourceFile : avatarFiles) {
      String fileName = sourceFile.getFileName().toString();
      String storagePath = AVATARS_DEST_PATH + fileName;

      if (avatarExistsInStorage(storagePath)) {
        log.debug("Avatar already exists in storage, skipping: {}", fileName);
        skipped++;
        continue;
      }

      if (uploadAvatarToStorage(sourceFile, storagePath)) {
        uploaded++;
      }
    }

    return new SeedResult(uploaded, skipped);
  }

  private boolean avatarExistsInStorage(String storagePath) {
    try (InputStream testStream = storageProvider.getFile(storagePath)) {
      return testStream != null;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean uploadAvatarToStorage(Path sourceFile, String storagePath) {
    String fileName = sourceFile.getFileName().toString();
    try (InputStream is = Files.newInputStream(sourceFile)) {
      storageProvider.uploadFile(storagePath, is, "image/webp");
      log.info("Uploaded avatar to MinIO: {}", storagePath);
      return true;
    } catch (Exception e) {
      log.error("Failed to upload avatar: {}", fileName, e);
      return false;
    }
  }

  private record SeedResult(int uploaded, int skipped) { }

  @Override
  public int getOrder() {
    return 0;
  }
}
