package com.safo.icos.modules.storage.infrastructure.adapter;

import com.safo.icos.modules.storage.api.StorageProvider;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioStorageProvider implements StorageProvider {

    private final MinioClient minioClient;

    @Value("${MINIO_BUCKET}")
    private String bucket;

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!found) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder().bucket(bucket).build()
                );
            }
        } catch (Exception e) {
            // Log or ignore if bucket exists
        }
    }

    @Override
    @SneakyThrows
    public void uploadFile(String path, InputStream inputStream, String contentType) {
        ensureBucketExists();
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .stream(inputStream, inputStream.available(), -1)
                        .contentType(contentType)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public void deleteFile(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception ignored) {
        }
    }

    @Override
    public String getAvatarPath(UUID companyId, String email, String originalFilename) {
        String emailSlug = email.replace("@", "_").replace(".", "_");
        String extension = getExtension(originalFilename);
        return String.format("%s/%s/avatar/avatar%s", companyId, emailSlug, extension);
    }

    @Override
    public String getDocumentPath(UUID companyId, String email, String docType, String filename) {
        String emailSlug = email.replace("@", "_").replace(".", "_");
        return String.format("%s/%s/documents/%s/%s", companyId, emailSlug, docType.toLowerCase(), filename);
    }

    @Override
    public InputStream getFile(String path) {
        try {
            ensureBucketExists();
            return minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            return null;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}
