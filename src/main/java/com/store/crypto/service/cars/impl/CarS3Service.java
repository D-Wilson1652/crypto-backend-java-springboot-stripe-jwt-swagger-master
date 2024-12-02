package com.store.crypto.service.cars.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarS3Service {

    private final S3Client s3Client;
    private final Environment environment;

    public CarS3Service(Environment environment) {
        this.environment = environment;

        log.info("Initializing S3 service");
        for (String profile : environment.getActiveProfiles()) {
            log.info("Active Profile: {}", profile);
        }

        log.info("Setting up S3 client");
        log.info("Bucket: {}", environment.getProperty("aws.s3.bucket-name"));

        this.s3Client = S3Client.builder()
                .region(Region.of("eu-north-1"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                environment.getProperty("aws.access-key-id"),
                                environment.getProperty("aws.secret-access-key")
                        )
                ))
                .build();
    }

    public String uploadFile(MultipartFile file, Long carId) {
        // Generate a unique file name with a UUID
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // Define the S3 key with directory structure
        String s3Key = "cars/" + carId + "/" + fileName;
        // Asynchronously upload the file to S3
        CompletableFuture.runAsync(() -> {
            try {
                log.info("Uploading file to S3: {}", s3Key);
                s3Client.putObject(PutObjectRequest.builder()
                                .bucket(environment.getProperty("aws.s3.bucket-name"))
                                .key(s3Key)
                                .build(),
                        RequestBody.fromBytes(file.getBytes()));
            } catch (IOException e) {
                log.error("Failed to upload file for the car: {} id to S3: {}", carId, e.getMessage());
                throw new RuntimeException("Failed to upload file to S3", e);
            }
        });
        return s3Key; // Return the S3 key (file path) for reference
    }

    public URI getFileUrl(String fileName) throws URISyntaxException {
        URL url = s3Client.utilities().getUrl(
                builder ->
                        builder.bucket(environment.getProperty("aws.s3.bucket-name")).key(fileName));
        return url.toURI();
    }

    public void deleteFile(String s3Key) {
        try {
            log.info("Deleting file from S3: {}", s3Key);
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(environment.getProperty("aws.s3.bucket-name"))
                    .key(s3Key)
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", s3Key, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public void deleteFilesByCarId(Long carId) {
        // Get a list of all files under the "real-estate/{carId}/" prefix
        List<String> keysToDelete = s3Client.listObjectsV2Paginator(builder ->
                        builder.bucket(environment.getProperty("aws.s3.bucket-name"))
                                .prefix("cars/" + carId + "/"))
                .contents()
                .stream()
                .map(S3Object::key)
                .collect(Collectors.toList());

        // Delete each file
        keysToDelete.forEach(this::deleteFile);
    }
}
